package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.property.PropertyQueryRequest;
import com.mrdotxin.propsmart.model.entity.Property;

import java.util.List;

/**
 * 房产信息服务
 */
public interface PropertyService extends IService<Property> {

    QueryWrapper<Property> getQueryWrapper(PropertyQueryRequest propertyQueryRequest);

    void validateProperty(Property property);

    Boolean hasPropertyInBuilding(Long buildingId);

    Boolean existsWithField(String fieldName, Object value);

    Property getByField(String fieldName, Object value);

    List<Property> listByField(String fieldName, Object value);
    
    /**
     * 根据业主ID获取房产ID列表
     *
     */
    List<Long> getPropertyIdsByOwnerId(Long ownerId);
}
