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
import com.mrdotxin.propsmart.model.dto.facility.FacilityAddRequest;
import com.mrdotxin.propsmart.model.dto.facility.FacilityQueryRequest;
import com.mrdotxin.propsmart.model.dto.facility.FacilityUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Facility;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import com.mrdotxin.propsmart.service.FacilityService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/facility")
@Api(tags = "设施管理")
public class FacilityController {

    @Resource
    private FacilityService facilityService;

    @Resource
    private FacilityReservationService facilityReservationService;

    @Resource
    private UserService userService;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    @ApiOperation(value = "添加设施")
    public BaseResponse<Boolean> addFacility(@RequestBody FacilityAddRequest facilityAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(facilityAddRequest), ErrorCode.PARAMS_ERROR);

        Facility facility = new Facility();
        BeanUtils.copyProperties(facilityAddRequest, facility);
        facilityService.validateFacility(facility);

        boolean result = facilityService.save(facility);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    @ApiOperation(value = "更新设施信息")
    public BaseResponse<Boolean> updateFacility(@RequestBody FacilityUpdateRequest facilityUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(facilityUpdateRequest), ErrorCode.PARAMS_ERROR);

        Facility facility = new Facility();
        BeanUtils.copyProperties(facilityUpdateRequest, facility);
        facilityService.validateFacility(facility);

        boolean result = facilityService.updateById(facility);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除设施")
    public BaseResponse<Boolean> deleteFacility(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        // 检查设施是否有预订记录
        boolean hasReservations = facilityReservationService.hasReservations(id);
        if (hasReservations) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该设施存在预订记录，无法删除");
        }

        boolean result = facilityService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据ID获取设施信息")
    public BaseResponse<Facility> getFacilityById(@RequestParam Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Facility facility = facilityService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(facility), ErrorCode.NOT_FOUND_ERROR, "设施不存在");
        return ResultUtils.success(facility);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取设施列表")
    public BaseResponse<Page<Facility>> listFacilityByPage(@RequestBody FacilityQueryRequest facilityQueryRequest, HttpServletRequest request) {
        long current = facilityQueryRequest.getCurrent();
        long size = facilityQueryRequest.getPageSize();
        Page<Facility> facilityPage = facilityService.page(new Page<>(current, size),
                facilityService.getQueryWrapper(facilityQueryRequest));
        return ResultUtils.success(facilityPage);
    }
}