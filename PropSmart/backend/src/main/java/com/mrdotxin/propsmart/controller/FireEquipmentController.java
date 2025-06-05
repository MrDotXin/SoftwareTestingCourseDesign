package com.mrdotxin.propsmart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentAddRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentInspectionRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentQueryRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentUpdateRequest;
import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.FireEquipmentService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "消防设备巡检管理")
@RestController
@RequestMapping("/fire-equipment")
public class FireEquipmentController {

    @Resource
    private FireEquipmentService fireEquipmentService;

    @Resource
    private UserService userService;

    /**
     * 获取消防设备信息
     *
     * @param id 设备ID
     * @param request HTTP请求
     * @return 消防设备信息
     */
    @GetMapping("/get/id")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取消防设备信息")
    public BaseResponse<FireEquipment> getFireEquipmentById(@RequestParam("id") Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        FireEquipment equipment = fireEquipmentService.getById(id);
        ThrowUtils.throwIf(equipment == null, ErrorCode.NOT_FOUND_ERROR, "设备不存在");

        return ResultUtils.success(equipment);
    }

    /**
     * 获取楼栋消防设备列表
     *
     * @param buildingId 楼栋ID
     * @param request HTTP请求
     * @return 消防设备列表
     */
    @GetMapping("/list/building/{buildingId}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取楼栋消防设备列表")
    public BaseResponse<List<FireEquipment>> getFireEquipmentByBuilding(@PathVariable("buildingId") Long buildingId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        List<FireEquipment> equipmentList = fireEquipmentService.getEquipmentByBuilding(buildingId);
        return ResultUtils.success(equipmentList);
    }

    /**
     * 获取需要巡检的设备列表
     *
     * @param daysThreshold 天数阈值，即多少天内需要巡检的设备
     * @param request HTTP请求
     * @return 需要巡检的设备列表
     */
    @GetMapping("/list/need-inspection")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取需要巡检的设备列表")
    public BaseResponse<List<FireEquipment>> getEquipmentNeedingInspection(@RequestParam(value = "daysThreshold", defaultValue = "3") int daysThreshold, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        List<FireEquipment> equipmentList = fireEquipmentService.getEquipmentNeedingInspection(daysThreshold);
        return ResultUtils.success(equipmentList);
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "添加消防设备")
    public BaseResponse<Boolean> addFireEquipment(@RequestBody FireEquipmentAddRequest fireEquipmentAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(fireEquipmentAddRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        FireEquipment equipment = new FireEquipment();
        BeanUtils.copyProperties(fireEquipmentAddRequest, equipment);
        
        // 设置创建和更新时间
        equipment.setCreateTime(new Date());
        equipment.setUpdateTime(new Date());
        
        // 设置初始状态
        equipment.setCurrentStatus("normal");
        
        boolean result = fireEquipmentService.save(equipment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");

        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新消防设备信息")
    public BaseResponse<Boolean> updateFireEquipment(@RequestBody FireEquipmentUpdateRequest fireEquipmentUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(fireEquipmentUpdateRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        FireEquipment equipment = new FireEquipment();
        BeanUtils.copyProperties(fireEquipmentUpdateRequest, equipment);
        
        // 设置更新时间
        equipment.setUpdateTime(new Date());

        boolean result = fireEquipmentService.updateById(equipment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");

        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除消防设备")
    public BaseResponse<Boolean> deleteFireEquipment(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);

        boolean result = fireEquipmentService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");

        return ResultUtils.success(true);
    }

    @PostMapping("/inspection")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "进行消防设备巡检")
    public BaseResponse<Boolean> performInspection(@RequestBody FireEquipmentInspectionRequest inspectionRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(inspectionRequest == null, ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        Long equipmentId = inspectionRequest.getEquipmentId();
        ThrowUtils.throwIf(equipmentId == null, ErrorCode.PARAMS_ERROR);

        boolean result = fireEquipmentService.performInspection(
                equipmentId, 
                loginUser.getId(), 
                inspectionRequest.getRemarks()
        );
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "巡检操作失败");

        return ResultUtils.success(true);
    }

    @PostMapping("/update-status")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新消防设备状态")
    public BaseResponse<Boolean> updateStatus(@RequestParam("equipmentId") Long equipmentId, 
                                            @RequestParam("status") String status, 
                                            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        ThrowUtils.throwIf(equipmentId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(status == null, ErrorCode.PARAMS_ERROR);

        boolean result = fireEquipmentService.updateEquipmentStatus(equipmentId, status);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "状态更新失败");

        return ResultUtils.success(true);
    }

    @PostMapping("/set-next-inspection")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "设置下次巡检日期")
    public BaseResponse<Boolean> setNextInspection(@RequestParam("equipmentId") Long equipmentId, 
                                                @RequestParam("nextDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date nextDate, 
                                                HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        ThrowUtils.throwIf(equipmentId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(nextDate == null, ErrorCode.PARAMS_ERROR);

        boolean result = fireEquipmentService.setNextInspectionDate(equipmentId, nextDate);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "设置失败");

        return ResultUtils.success(true);
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取消防设备列表")
    public BaseResponse<Page<FireEquipment>> listFireEquipmentByPage(@RequestBody FireEquipmentQueryRequest fireEquipmentQueryRequest, 
                                                                   HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        String status = fireEquipmentQueryRequest.getStatus();
        long current = fireEquipmentQueryRequest.getCurrent();
        long size = fireEquipmentQueryRequest.getPageSize();

        Page<FireEquipment> equipmentPage = fireEquipmentService.pageByStatus(status, current, size);
        return ResultUtils.success(equipmentPage);
    }
} 