package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.mapper.FacilityReservationMapper;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @description 针对表【facilityreservation(设施预订)】的数据库操作Service实现
 * @createDate 2025-06-03 21:37:35
 */
@Service
public class FacilityReservationServiceImpl extends ServiceImpl<FacilityReservationMapper, FacilityReservation>
        implements FacilityReservationService {

    @Override
    public boolean hasReservations(Long facilityId) {
        QueryWrapper<FacilityReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("facilityId", facilityId);
        return this.baseMapper.exists(queryWrapper);
    }


    @Override
    public Boolean existsWithField(String fieldName, Object value) {
        QueryWrapper<FacilityReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public FacilityReservation getByFiled(String fieldName, Object value) {
        QueryWrapper<FacilityReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.selectOne(queryWrapper);
    }
}




