package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.BuildingMapper;
import com.mrdotxin.propsmart.model.dto.building.BuildingQueryRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.geo.GeoPoint;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Administrator
* @description 针对表【building(楼栋信息)】的数据库操作Service实现
* @createDate 2025-06-03 18:45:18
*/
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building>
    implements BuildingService{

    @Override
    public Boolean isBuildingExist(String buildingName) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(buildingName), "buildingName", buildingName);

        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public Building getByBuildingName(String buildingName) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buildingName", buildingName);

        return this.baseMapper.selectOne(queryWrapper);
    }


    @Override
    public void validateBuilding(Building building) {
        ThrowUtils.throwIf(ObjectUtil.isNull(building), ErrorCode.PARAMS_ERROR);
        String buildingName = building.getBuildingName();
        ThrowUtils.throwIf(StrUtil.isBlank(buildingName), ErrorCode.PARAMS_ERROR, "楼栋名称不能为空");
        
        // 验证地理位置
        ThrowUtils.throwIf(ObjectUtil.isNull(building.getLocation()), 
                ErrorCode.PARAMS_ERROR, "楼栋地理位置不能为空");

        // 检查楼栋名称是否重复
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buildingName", buildingName);
        if (building.getId() != null) {
            queryWrapper.ne("id", building.getId());
        }
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "楼栋名称已存在");
    }

    @Override
    public QueryWrapper<Building> getQueryWrapper(BuildingQueryRequest buildingQueryRequest) {
        if (buildingQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String buildingName = buildingQueryRequest.getBuildingName();
        Integer totalFloors = buildingQueryRequest.getTotalFloors();
        String sortField = buildingQueryRequest.getSortField();
        String sortOrder = buildingQueryRequest.getSortOrder();
        
        // 空间查询不在这里处理，而是在专门的方法中处理
        
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(buildingName), "buildingName", buildingName);
        queryWrapper.eq(ObjectUtil.isNotNull(totalFloors), "totalLevels", totalFloors);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                          sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                          sortField);
        return queryWrapper;
    }

    @Override
    public Boolean existsWithField(String fieldName, Object value) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public Building getByField(String fieldName, Object value) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.selectOne(queryWrapper);
    }
    
    @Override
    public List<Building> findBuildingsWithinDistance(GeoPoint centerPoint, double distanceInMeters) {
        if (centerPoint == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "中心点不能为空");
        }
        if (distanceInMeters <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索距离必须大于0");
        }
        
        return this.baseMapper.findBuildingsWithinDistance(centerPoint.toWkt(), distanceInMeters);
    }
    
    @Override
    public List<Building> findBuildingsInPolygon(List<GeoPoint> points) {
        if (points == null || points.size() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "多边形至少需要3个点");
        }
        
        String polygonWkt = GeoPoint.createPolygonWkt(points);
        return this.baseMapper.findBuildingsInPolygon(polygonWkt);
    }
    
    @Override
    public Boolean isPointInBuilding(GeoPoint point, Long buildingId) {
        if (point == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "点不能为空");
        }
        if (buildingId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "楼栋ID不能为空");
        }
        if (buildingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "楼栋ID必须大于0");
        }
        
        // 方法一：使用MySQL空间函数ST_Contains直接在数据库中判断
        Integer result = this.baseMapper.isPointInBuilding(point.toWkt(), buildingId);
        if (result != null) {
            return result == 1;
        }
        
        // 方法二：如果数据库查询失败，则使用Java代码进行判断
        List<GeoPoint> polygonPoints = getBuildingPolygonPoints(buildingId);
        if (polygonPoints == null || polygonPoints.isEmpty()) {
            return false;
        }
        
        return GeoPoint.isPointInPolygon(point, polygonPoints);
    }
    
    @Override
    public List<GeoPoint> getBuildingPolygonPoints(Long buildingId) {
        if (buildingId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "楼栋ID不能为空");
        }
        if (buildingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "楼栋ID必须大于0");
        }
        
        String polygonWkt = this.baseMapper.getBuildingPolygonWkt(buildingId);
        if (polygonWkt == null || polygonWkt.isEmpty()) {
            return null;
        }
        
        // 解析WKT格式的多边形坐标
        return parsePolygonWktToPoints(polygonWkt);
    }
    
    /**
     * 解析WKT格式的多边形坐标为点列表
     * 
     * @param polygonWkt 多边形的WKT表示（如 "POLYGON((x1 y1, x2 y2, ...))"）
     * @return 点列表
     */
    private List<GeoPoint> parsePolygonWktToPoints(String polygonWkt) {
        List<GeoPoint> points = new ArrayList<>();
        
        // 使用正则表达式来提取坐标
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(polygonWkt.trim().toUpperCase());
        
        if (matcher.find() && matcher.groupCount() >= 1) {
            String coordinates = matcher.group(1);
            if (matcher.find() && matcher.groupCount() >= 1) {
                coordinates = matcher.group(1);
            }
            
            String[] pointPairs = coordinates.split(",");
            for (String pointPair : pointPairs) {
                String[] xy = pointPair.trim().split("\\s+");
                if (xy.length >= 2) {
                    try {
                        double x = Double.parseDouble(xy[0]);
                        double y = Double.parseDouble(xy[1]);
                        points.add(new GeoPoint(x, y));
                    } catch (NumberFormatException e) {
                        // 忽略无效的坐标
                    }
                }
            }
        }
        
        return points;
    }
}




