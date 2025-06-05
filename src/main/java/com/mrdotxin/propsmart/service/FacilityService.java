package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.facility.FacilityQueryRequest;
import com.mrdotxin.propsmart.model.entity.Facility;

/**
 * @author Administrator
 * @description 针对表【facilities(小区设施)】的数据库操作Service
 * @createDate 2025-06-03 18:27:14
 */
public interface FacilityService extends IService<Facility> {

    /**
     * @param facility
     */
    void validateFacility(Facility facility);

    /**
     * @param facilityQueryRequest
     * @return
     */
    public QueryWrapper<Facility> getQueryWrapper(FacilityQueryRequest facilityQueryRequest);

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
    Facility getByFiled(String fieldName, Object value);
}
