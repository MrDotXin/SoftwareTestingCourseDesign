package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.PropertyMapper;
import com.mrdotxin.propsmart.model.dto.property.PropertyQueryRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【Property(房产信息)】的数据库操作Service实现
 * @createDate 2025-06-03 18:27:32
 */
@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property>
        implements PropertyService {
    @Resource
    private BuildingService buildingService;

    @Override
    public QueryWrapper<Property> getQueryWrapper(PropertyQueryRequest propertyQueryRequest) {
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyQueryRequest), ErrorCode.OPERATION_ERROR, "不能传入空值");

        Long ownerId = propertyQueryRequest.getOwnerId();
        Long ownerIdentity = propertyQueryRequest.getOwnerIdentity();
        String buildingName = propertyQueryRequest.getBuildingName();
        String unitNumber = propertyQueryRequest.getUnitNumber();
        String roomNumber = propertyQueryRequest.getRoomNumber();
        String sortField = propertyQueryRequest.getSortField();
        String sortOrder = propertyQueryRequest.getSortOrder();

        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(ownerId) && ownerId > 0, "ownerId", ownerId);
        queryWrapper.eq(ObjectUtil.isNotNull(ownerIdentity) && ownerIdentity > 0, "ownerIdentity", ownerIdentity);
        queryWrapper.like(StrUtil.isNotBlank(buildingName), "buildingName", buildingName);
        queryWrapper.eq(StrUtil.isNotBlank(unitNumber), "unitNumber", unitNumber);
        queryWrapper.eq(StrUtil.isNotBlank(roomNumber), "roomNumber", roomNumber);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return null;
    }

    @Override
    public void validateProperty(Property property) {
        ThrowUtils.throwIf(ObjectUtil.isNull(property), ErrorCode.PARAMS_ERROR);

        Long buildingId = property.getBuildingId();
        String unitNumber = property.getUnitNumber();
        String roomNumber = property.getRoomNumber();

        ThrowUtils.throwIf(ObjectUtil.isNull(unitNumber), ErrorCode.PARAMS_ERROR, "单元号不得为空");
        ThrowUtils.throwIf(ObjectUtil.isNull(roomNumber), ErrorCode.PARAMS_ERROR, "房间号不得为空");

        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buildingId", buildingId);
        queryWrapper.eq("unitNumber", unitNumber);
        queryWrapper.eq("roomNumber", roomNumber);

        Property oldProperty = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(oldProperty) && !oldProperty.getId().equals(property.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前位置的房产已存在!");
        }
    }

    @Override
    public Boolean hasPropertyInBuilding(Long buildingId) {
        QueryWrapper<Property> propertyQueryWrapper = new QueryWrapper<>();
        propertyQueryWrapper.eq("buildingId", buildingId);

        return this.baseMapper.exists(propertyQueryWrapper);
    }


    @Override
    public Boolean existsWithField(String fieldName, Object value) {
        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public Property getByFiled(String fieldName, Object value) {
        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Property> listByFiled(String fieldName, Object value) {
        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);

        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Long> getPropertyIdsByOwnerId(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            return new ArrayList<>();
        }
        
        // 根据业主ID查询房产
        // 注意：根据数据库结构，Property表中可能没有直接关联业主ID的字段
        // 这里假设使用ownerIdentity字段关联，实际应用中需要根据数据库结构调整
        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ownerIdentity", ownerId);
        
        List<Property> properties = this.list(queryWrapper);
        
        // 提取房产ID列表
        return properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
    }
}




