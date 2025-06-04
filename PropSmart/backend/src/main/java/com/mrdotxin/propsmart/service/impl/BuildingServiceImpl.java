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
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.stereotype.Service;

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
        String address = buildingQueryRequest.getAddress();
        String sortField = buildingQueryRequest.getSortField();
        String sortOrder = buildingQueryRequest.getSortOrder();

        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(buildingName), "buildingName", buildingName);
        queryWrapper.like(StrUtil.isNotBlank(address), "address", address);
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
    public Building getByFiled(String fieldName, Object value) {
        QueryWrapper<Building> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.selectOne(queryWrapper);
    }
}




