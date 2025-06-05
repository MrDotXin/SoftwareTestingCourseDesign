package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.property.PropertyQueryRequest;
import com.mrdotxin.propsmart.model.entity.Property;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【Property(房产信息)】的数据库操作Service
 * @createDate 2025-06-03 18:27:32
 */
public interface PropertyService extends IService<Property> {

    /**
     * @param propertyQueryRequest
     * @return
     */
    QueryWrapper<Property> getQueryWrapper(PropertyQueryRequest propertyQueryRequest);

    /**
     * @param property
     * @return
     */
    void validateProperty(Property property);

    /**
     * @param buildingId
     * @return
     */
    Boolean hasPropertyInBuilding(Long buildingId);

    /**
     *
     * @param fieldName
     * @param value
     * @return
     */
    Boolean existsWithField(String fieldName, Object value);

    /**
     *
     * @param fieldName
     * @param value
     * @return
     */
    Property getByFiled(String fieldName, Object value);

    List<Property> listByFiled(String fieldName, Object value);
    
    /**
     * 根据业主ID获取房产ID列表
     *
     * @param ownerId 业主ID
     * @return 房产ID列表
     */
    List<Long> getPropertyIdsByOwnerId(Long ownerId);
}
