package com.mrdotxin.propsmart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentAddRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentQueryRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentUpdateRequest;
import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.FireEquipmentService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 消防设备管理接口
 */
@RestController
@RequestMapping("/fire_equipment")
@Api(tags = "消防设备管理")
public class FireEquipmentController {

    @Resource
    private FireEquipmentService fireEquipmentService;

    @Resource
    private UserService userService;

    /**
     * 添加消防设备
     *
     * @param fireEquipmentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "添加消防设备")
    public BaseResponse<Long> addFireEquipment(@RequestBody FireEquipmentAddRequest fireEquipmentAddRequest, 
                                         HttpServletRequest request) {
        if (fireEquipmentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long fireEquipmentId = fireEquipmentService.addFireEquipment(fireEquipmentAddRequest, loginUser);
        return ResultUtils.success(fireEquipmentId);
    }
    
    /**
     * 删除消防设备
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除消防设备")
    public BaseResponse<Boolean> deleteFireEquipment(@RequestBody Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = fireEquipmentService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    
    /**
     * 更新消防设备
     *
     * @param fireEquipmentUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新消防设备")
    public BaseResponse<Boolean> updateFireEquipment(@RequestBody FireEquipmentUpdateRequest fireEquipmentUpdateRequest, 
                                              HttpServletRequest request) {
        if (fireEquipmentUpdateRequest == null || fireEquipmentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = fireEquipmentService.updateFireEquipment(fireEquipmentUpdateRequest, loginUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    
    /**
     * 根据ID获取消防设备
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据ID获取消防设备")
    public BaseResponse<FireEquipment> getFireEquipmentById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        FireEquipment fireEquipment = fireEquipmentService.getById(id);
        ThrowUtils.throwIf(fireEquipment == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(fireEquipment);
    }
    
    /**
     * 分页获取消防设备列表
     *
     * @param fireEquipmentQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取消防设备列表")
    public BaseResponse<Page<FireEquipment>> listFireEquipmentByPage(@RequestBody FireEquipmentQueryRequest fireEquipmentQueryRequest) {
        long current = fireEquipmentQueryRequest.getCurrent();
        long size = fireEquipmentQueryRequest.getPageSize();
        Page<FireEquipment> fireEquipmentPage = fireEquipmentService.page(new Page<>(current, size),
                fireEquipmentService.getQueryWrapper(fireEquipmentQueryRequest));
        return ResultUtils.success(fireEquipmentPage);
    }
    
    /**
     * 获取所有待巡检的设备
     *
     * @return
     */
    @GetMapping("/pending")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取待巡检设备")
    public BaseResponse<List<FireEquipment>> getPendingInspectionEquipment() {
        List<FireEquipment> pendingEquipment = fireEquipmentService.getPendingInspectionEquipment();
        return ResultUtils.success(pendingEquipment);
    }
} 