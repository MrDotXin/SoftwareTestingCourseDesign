package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.building.BuildingQueryRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.geo.GeoPoint;

import java.util.List;

/**
* 楼栋信息的数据库操作Service
*
*/
public interface BuildingService extends IService<Building> {

    Boolean isBuildingExist(String buildingName);

    Building getByBuildingName(String buildingName);

    /**
     * 根据查询条件生成QueryWrapper
     * @param buildingQueryRequest 查询请求
     * @return QueryWrapper对象
     */
    QueryWrapper<Building> getQueryWrapper(BuildingQueryRequest buildingQueryRequest);

    /**
     * 验证楼栋信息
     * @param building 楼栋对象
     */
    void validateBuilding(Building building);
    
    /**
     * 检查指定字段是否存在
     * @param fieldName 字段名称
     * @param value 字段值
     * @return 是否存在
     */
    Boolean existsWithField(String fieldName, Object value);

    /**
     * 根据字段获取楼栋信息
     * @param fieldName 字段名称
     * @param value 字段值
     * @return 楼栋对象
     */
    Building getByField(String fieldName, Object value);
    
    /**
     * 查询指定距离范围内的楼栋
     * @param centerPoint 中心点
     * @param distanceInMeters 距离（米）
     * @return 符合条件的楼栋列表
     */
    List<Building> findBuildingsWithinDistance(GeoPoint centerPoint, double distanceInMeters);
    
    /**
     * 根据多边形区域查询楼栋
     * @param points 构成多边形的点列表
     * @return 符合条件的楼栋列表
     */
    List<Building> findBuildingsInPolygon(List<GeoPoint> points);
    
    /**
     * 检查点是否在楼栋内
     * @param point 要检查的点
     * @param buildingId 楼栋ID
     * @return 如果点在楼栋内，则返回true；否则返回false
     */
    Boolean isPointInBuilding(GeoPoint point, Long buildingId);
    
    /**
     * 获取楼栋的多边形点列表
     * @param buildingId 楼栋ID
     * @return 多边形的点列表，如果找不到楼栋或楼栋没有多边形，则返回null
     */
    List<GeoPoint> getBuildingPolygonPoints(Long buildingId);
}
