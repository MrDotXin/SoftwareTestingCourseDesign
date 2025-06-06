package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.facility.reservation.FacilityReservationQueryRequest;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
* 设施预订服务
*/
public interface FacilityReservationService extends IService<FacilityReservation> {

    boolean hasReservations(Long facilityId);

    Boolean existsWithField(String fieldName, Object value);

    FacilityReservation getByFiled(String fieldName, Object value);

    /**
     * 添加设施预订
     */
    long addReservation(FacilityReservation facilityReservation, Long userId);

    /**
     * 处理设施预订
     */
    boolean reviewReservation(FacilityReservation facilityReservation, Long reviewerId);

    /**
     * 检查设施是否可预订（容量检查）
     * @return true if available, false if not
     */
    boolean checkFacilityAvailability(Integer facilityId, Date reservationTime, Integer duration);

    /**
     * 获取查询条件
     */
    QueryWrapper<FacilityReservation> getQueryWrapper(FacilityReservationQueryRequest facilityReservationQueryRequest);
}
