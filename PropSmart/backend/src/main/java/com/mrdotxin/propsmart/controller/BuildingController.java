package com.mrdotxin.propsmart.controller;

import cn.hutool.core.collection.CollUtil;
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
import com.mrdotxin.propsmart.model.geo.GeoPoint;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
        ThrowUtils.throwIf(ObjectUtil.isNull(buildingAddRequest.getLocation()), ErrorCode.PARAMS_ERROR, "楼栋地理位置不能为空");

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
        ThrowUtils.throwIf(buildingUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR, "楼栋ID不能为空");

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

    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取楼栋列表")
    public BaseResponse<Page<Building>> listBuildingByPage(@RequestBody BuildingQueryRequest buildingQueryRequest, HttpServletRequest request) {
        long current = buildingQueryRequest.getCurrent();
        long size = buildingQueryRequest.getPageSize();
        Page<Building> buildingPage = buildingService.page(new Page<>(current, size),
                buildingService.getQueryWrapper(buildingQueryRequest));
        return ResultUtils.success(buildingPage);
    }
    
    @PostMapping("/nearby")
    @ApiOperation(value = "获取附近楼栋")
    public BaseResponse<List<Building>> findNearbyBuildings(@RequestBody BuildingQueryRequest buildingQueryRequest, 
                                                            HttpServletRequest request) {
        GeoPoint centerPoint = buildingQueryRequest.getCenterPoint();
        Double searchRadius = buildingQueryRequest.getSearchRadius();
        
        ThrowUtils.throwIf(centerPoint == null, ErrorCode.PARAMS_ERROR, "搜索中心点不能为空");
        ThrowUtils.throwIf(searchRadius == null || searchRadius <= 0, 
                ErrorCode.PARAMS_ERROR, "搜索半径必须大于0");
        
        List<Building> buildings = buildingService.findBuildingsWithinDistance(centerPoint, searchRadius);
        
        return ResultUtils.success(buildings);
    }
    
    @PostMapping("/polygon")
    @ApiOperation(value = "获取多边形区域内的楼栋")
    public BaseResponse<List<Building>> findBuildingsInPolygon(@RequestBody List<GeoPoint> polygonPoints, 
                                                               HttpServletRequest request) {
        ThrowUtils.throwIf(CollUtil.isEmpty(polygonPoints) || polygonPoints.size() < 3, 
                ErrorCode.PARAMS_ERROR, "多边形至少需要3个点");
        
        List<Building> buildings = buildingService.findBuildingsInPolygon(polygonPoints);
        
        return ResultUtils.success(buildings);
    }
    
    @PostMapping("/isPointInBuilding")
    @ApiOperation(value = "判断点是否在建筑物内")
    public BaseResponse<Boolean> isPointInBuilding(@RequestBody Map<String, Object> requestData, 
                                                  HttpServletRequest request) {
        // 从请求数据中提取点坐标和楼栋ID
        Double x = (Double) requestData.get("x");
        Double y = (Double) requestData.get("y");
        Long buildingId = Long.valueOf(requestData.get("buildingId").toString());
        
        ThrowUtils.throwIf(x == null || y == null, ErrorCode.PARAMS_ERROR, "点坐标不能为空");
        ThrowUtils.throwIf(buildingId == null || buildingId <= 0, ErrorCode.PARAMS_ERROR, "楼栋ID不能为空");
        
        GeoPoint point = new GeoPoint(x, y);
        Boolean isInside = buildingService.isPointInBuilding(point, buildingId);
        
        return ResultUtils.success(isInside);
    }
    
    @GetMapping("/polygon/{buildingId}")
    @ApiOperation(value = "获取楼栋的多边形点列表")
    public BaseResponse<List<GeoPoint>> getBuildingPolygonPoints(@PathVariable Long buildingId, 
                                                               HttpServletRequest request) {
        ThrowUtils.throwIf(buildingId == null || buildingId <= 0, ErrorCode.PARAMS_ERROR, "楼栋ID不能为空");
        
        List<GeoPoint> points = buildingService.getBuildingPolygonPoints(buildingId);
        ThrowUtils.throwIf(points == null || points.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "楼栋多边形不存在");
        
        return ResultUtils.success(points);
    }
}
