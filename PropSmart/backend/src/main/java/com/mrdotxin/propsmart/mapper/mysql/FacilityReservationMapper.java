package com.mrdotxin.propsmart.mapper.mysql;

import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
* 设施预订的数据库操作Mapper
*/
public interface FacilityReservationMapper extends BaseMapper<FacilityReservation> {

    @Select("CALL GetMaxConcurrentPeople(#{facilityId}, #{startTime}, #{endTime})")
    Long getMaxConcurrentReservationId(@Param("facilityId") Long facilityId, @Param("startTime")Date startTime, @Param("endTime") Date endTime);

}




