package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.facility.reservation.FacilityReservationQueryRequest;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

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

    /**
     * 添加设施预订
     * @param facilityReservation
     * @param userId
     * @return
     */
    long addReservation(FacilityReservation facilityReservation, Long userId);

    /**
     * 处理设施预订
     * @param facilityReservation
     * @param reviewerId
     * @return
     */
    boolean reviewReservation(FacilityReservation facilityReservation, Long reviewerId);

    /**
     * 检查设施是否可预订（容量检查）
     * @param facilityId
     * @param reservationTime
     * @param duration
     * @return true if available, false if not
     */
    boolean checkFacilityAvailability(Integer facilityId, Date reservationTime, Integer duration);

    /**
     * 获取查询条件
     * @param facilityReservationQueryRequest
     * @return
     */
    QueryWrapper<FacilityReservation> getQueryWrapper(FacilityReservationQueryRequest facilityReservationQueryRequest);
}
