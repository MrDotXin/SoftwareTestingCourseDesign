package com.mrdotxin.propsmart.service;

import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【facilityreservation(设施预订)】的数据库操作Service
* @createDate 2025-06-03 21:37:35
*/
public interface FacilityReservationService extends IService<FacilityReservation> {

    /**
     *
     * @param facilityId
     * @return
     */
    boolean hasReservations(Long facilityId);

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
    FacilityReservation getByFiled(String fieldName, Object value);
}
