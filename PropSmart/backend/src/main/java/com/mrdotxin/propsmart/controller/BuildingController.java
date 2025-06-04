package com.mrdotxin.propsmart.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.building.BuildingAddRequest;
import com.mrdotxin.propsmart.model.dto.building.BuildingQueryRequest;
import com.mrdotxin.propsmart.model.dto.building.BuildingUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/building")
@Api(tags = "楼栋管理")
public class BuildingController {

    @Resource
    private BuildingService buildingService;

    @Resource
    private PropertyService propertyService;

    @Resource
    private UserService userService;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    @ApiOperation(value = "添加楼栋")
    public BaseResponse<Boolean> addBuilding(@RequestBody BuildingAddRequest buildingAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(buildingAddRequest), ErrorCode.PARAMS_ERROR);

        Building building = new Building();
        BeanUtils.copyProperties(buildingAddRequest, building);
        buildingService.validateBuilding(building);

        boolean result = buildingService.save(building);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    @ApiOperation(value = "更新楼栋信息")
    public BaseResponse<Boolean> updateBuilding(@RequestBody BuildingUpdateRequest buildingUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(buildingUpdateRequest), ErrorCode.PARAMS_ERROR);

        Building building = new Building();
        BeanUtils.copyProperties(buildingUpdateRequest, building);
        buildingService.validateBuilding(building);

        boolean result = buildingService.updateById(building);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除楼栋")
    public BaseResponse<Boolean> deleteBuilding(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        // 检查楼栋下是否有房产
        boolean hasProperties = propertyService.hasPropertyInBuilding(id);
        if (hasProperties) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该楼栋下存在房产，无法删除");
        }

        boolean result = buildingService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据ID获取楼栋信息")
    public BaseResponse<Building> getBuildingById(@RequestParam Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Building building = buildingService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(building), ErrorCode.NOT_FOUND_ERROR, "楼栋不存在");
        return ResultUtils.success(building);
    }

    @GetMapping("/get/name")
    @ApiOperation(value = "根据名称获取楼栋信息")
    public BaseResponse<Building> getBuildingByName(@RequestParam String buildingName, HttpServletRequest request) {
        ThrowUtils.throwIf(StrUtil.isBlank(buildingName), ErrorCode.PARAMS_ERROR);
        Building building = buildingService.getByBuildingName(buildingName);
        ThrowUtils.throwIf(ObjectUtil.isNull(building), ErrorCode.NOT_FOUND_ERROR, "楼栋不存在");
        return ResultUtils.success(building);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取楼栋列表")
    public BaseResponse<Page<Building>> listBuildingByPage(@RequestBody BuildingQueryRequest buildingQueryRequest, HttpServletRequest request) {
        long current = buildingQueryRequest.getCurrent();
        long size = buildingQueryRequest.getPageSize();
        Page<Building> buildingPage = buildingService.page(new Page<>(current, size),
                buildingService.getQueryWrapper(buildingQueryRequest));
        return ResultUtils.success(buildingPage);
    }
}
