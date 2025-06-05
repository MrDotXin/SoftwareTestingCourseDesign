package com.mrdotxin.propsmart.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.repairOrder.RepairOrderQueryRequest;
import com.mrdotxin.propsmart.model.dto.repairOrder.RepairOrderStatusUpdateRequest;
import com.mrdotxin.propsmart.model.dto.repairOrder.RepairOrderSubmitRequest;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.RepairOrder;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.RepairOrderStatusEnum;
import com.mrdotxin.propsmart.model.vo.RepairOrderVO;
import com.mrdotxin.propsmart.websocket.NotificationService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.RepairOrderService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repair")
@Api(tags = "报修管理")
public class RepairOrderController {

    @Resource
    private RepairOrderService repairOrderService;

    @Resource
    private UserService userService;

    @Resource
    private PropertyService propertyService;
    
    @Resource
    private NotificationService notificationService;

    @PostMapping("/submit")
    @AuthCheck(mustOwner = true)
    @ApiOperation(value = "用户提交报修申请")
    public BaseResponse<Long> submitRepairOrder(@RequestBody RepairOrderSubmitRequest submitRequest,
                                                HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(submitRequest), ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!loginUser.getIsOwner(), ErrorCode.NO_AUTH_ERROR, "仅业主有权限报修");

        RepairOrder repairOrder = new RepairOrder();
        BeanUtils.copyProperties(submitRequest, repairOrder);
        repairOrder.setUserId(loginUser.getId());

        // 验证用户是否有权限提交该房产的报修
        if (!userService.isAdmin(loginUser)) {
            validateUserPropertyAccess(loginUser.getUserIdCardNumber(), submitRequest.getPropertyId());
        }

        repairOrderService.validateRepairOrder(repairOrder);
        boolean result = repairOrderService.save(repairOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "提交失败");
        
        // 发送WebSocket通知给管理员
        notificationService.handleRepairOrderNotification(repairOrder, true);

        return ResultUtils.success(repairOrder.getId());
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update/status")
    @ApiOperation(value = "管理员审核报修")
    public BaseResponse<Boolean> reviewRepairOrder(@RequestBody RepairOrderStatusUpdateRequest statusUpdateRequest,
                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(statusUpdateRequest), ErrorCode.PARAMS_ERROR);
        if (statusUpdateRequest.getStatus().equals(RepairOrderStatusEnum.COMPLETED.getValue()) ||
                statusUpdateRequest.getStatus().equals(RepairOrderStatusEnum.CANCELLED.getValue())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "错误, 审核结果只能是completed或者cancelled");
        }

        User adminUser = userService.getLoginUser(request);
        RepairOrder repairOrder = repairOrderService.getById(statusUpdateRequest.getId());
        ThrowUtils.throwIf(ObjectUtil.isNull(repairOrder), ErrorCode.NOT_FOUND_ERROR, "报修单不存在");

        // 更新状态
        repairOrder.setStatus(statusUpdateRequest.getStatus());
        repairOrder.setReviewerId(adminUser.getId());
        repairOrder.setReviewTime(new Date());
        repairOrder.setReviewMessage(statusUpdateRequest.getReviewMessage());

        boolean result = repairOrderService.updateById(repairOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        
        // 发送WebSocket通知给用户
        notificationService.handleRepairOrderNotification(repairOrder, false);

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除报修单")
    public BaseResponse<Boolean> deleteRepairOrder(@RequestBody DeleteRequest deleteRequest,
                                                   HttpServletRequest request) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        boolean result = repairOrderService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @AuthCheck(mustOwner = true)
    @ApiOperation(value = "获取报修单详情")
    public BaseResponse<RepairOrderVO> getRepairOrderById(@RequestParam Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        RepairOrder repairOrder = repairOrderService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(repairOrder), ErrorCode.NOT_FOUND_ERROR, "报修单不存在");

        // 非管理员只能查看自己的报修单
        if (!userService.isAdmin(loginUser) && !repairOrder.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看");
        }

        RepairOrderVO vo = repairOrderService.getRepairOrderVO(repairOrder);
        return ResultUtils.success(vo);
    }

    @GetMapping("/list/my")
    @AuthCheck(mustOwner = true)
    @ApiOperation(value = "用户查看自己的报修单")
    public BaseResponse<Page<RepairOrderVO>> listMyRepairOrdersPage(@RequestBody RepairOrderQueryRequest queryRequest,
                                                                    HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(queryRequest), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);

        queryRequest.setUserId(loginUser.getId());

        Page<RepairOrder> repairOrderPage
                = repairOrderService.page(new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize()),
                repairOrderService.getQueryWrapper(queryRequest));

        Page<RepairOrderVO> voPage = new Page<>(repairOrderPage.getCurrent(), repairOrderPage.getSize(), repairOrderPage.getTotal());
        List<RepairOrderVO> voList = repairOrderService.getRepairOrderVOList(repairOrderPage.getRecords());
        voPage.setRecords(voList);

        return ResultUtils.success(voPage);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    @ApiOperation(value = "管理员分页查询报修单")
    public BaseResponse<Page<RepairOrder>> listRepairOrderByPage(@RequestBody RepairOrderQueryRequest queryRequest,
                                                                 HttpServletRequest request) {
        Page<RepairOrder> repairOrderPage = repairOrderService.page(new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize()),
                repairOrderService.getQueryWrapper(queryRequest));

        Page<RepairOrder> voPage = new Page<>(repairOrderPage.getCurrent(), repairOrderPage.getSize(), repairOrderPage.getTotal());
        return ResultUtils.success(voPage);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/stats/status")
    @ApiOperation(value = "报修单状态统计")
    public BaseResponse<Map<String, Long>> getRepairOrderStatusStats() {
        Map<String, Long> stats = repairOrderService.getStatusStatistics();
        return ResultUtils.success(stats);
    }

    /**
     * 验证用户是否有权限提交该房产的报修
     */
    private void validateUserPropertyAccess(String userIdCard, Long propertyId) {
        // 检查用户是否关联该房产
        Property property = propertyService.getById(propertyId);
        ThrowUtils.throwIf(ObjectUtil.isNull(property), ErrorCode.PARAMS_ERROR, "房产不存在");
        if (ObjectUtil.isNull(property.getOwnerIdentity()) || !userIdCard.equals(property.getOwnerIdentity())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有权限");
        }
    }
}