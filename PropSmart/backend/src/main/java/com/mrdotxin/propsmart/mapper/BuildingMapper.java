package com.mrdotxin.propsmart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mrdotxin.propsmart.model.entity.Building;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【building(楼栋信息)】的数据库操作Mapper
 * @createDate 2025-06-03 18:45:18
 * @Entity generator.domain.Building
 */
public interface BuildingMapper extends BaseMapper<Building> {
    
    /**
     * 查询指定范围内的楼栋
     * @param centerPoint 中心点
     * @param distanceInMeters 距离（米）
     * @return 符合条件的楼栋列表
     */
    @Select("SELECT *, ST_AsText(location) as location FROM building " +
            "WHERE ST_Distance_Sphere(location, ST_GeomFromText(#{centerPoint})) <= #{distanceInMeters}")
    List<Building> findBuildingsWithinDistance(@Param("centerPoint") String centerPoint, 
                                             @Param("distanceInMeters") double distanceInMeters);
    
    /**
     * 按多边形区域查询楼栋
     * @param polygonWkt 多边形的WKT表示
     * @return 符合条件的楼栋列表
     */
    @Select("SELECT *, ST_AsText(location) as location FROM building " +
            "WHERE ST_Contains(ST_GeomFromText(#{polygonWkt}), location)")
    List<Building> findBuildingsInPolygon(@Param("polygonWkt") String polygonWkt);
    
    /**
     * 获取楼栋的多边形坐标点
     * @param buildingId 楼栋ID
     * @return 包含楼栋多边形坐标的字符串
     */
    @Select("SELECT ST_AsText(location) FROM building WHERE id = #{buildingId}")
    String getBuildingPolygonWkt(@Param("buildingId") Long buildingId);
    
    /**
     * 检查点是否在楼栋内
     * @param pointWkt 点的WKT表示
     * @param buildingId 楼栋ID
     * @return 如果点在楼栋内，则返回1，否则返回0
     */
    @Select("SELECT ST_Contains(location, ST_GeomFromText(#{pointWkt})) FROM building WHERE id = #{buildingId}")
    Integer isPointInBuilding(@Param("pointWkt") String pointWkt, @Param("buildingId") Long buildingId);
}




