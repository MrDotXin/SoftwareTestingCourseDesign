package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.facility.FacilityQueryRequest;
import com.mrdotxin.propsmart.model.entity.Facility;

/**
 * 小区设施服务
 */
public interface FacilityService extends IService<Facility> {

    void validateFacility(Facility facility);

    public QueryWrapper<Facility> getQueryWrapper(FacilityQueryRequest facilityQueryRequest);

    Boolean existsWithField(String fieldName, Object value);

    Facility getByFiled(String fieldName, Object value);
}
