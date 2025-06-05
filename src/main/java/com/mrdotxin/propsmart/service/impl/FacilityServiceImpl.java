package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.FacilityMapper;
import com.mrdotxin.propsmart.model.dto.facility.FacilityQueryRequest;
import com.mrdotxin.propsmart.model.entity.Facility;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import com.mrdotxin.propsmart.service.FacilityService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @description 针对表【facilities(小区设施)】的数据库操作Service实现
 * @createDate 2025-06-03 18:27:14
 */
@Service
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, Facility>
        implements FacilityService {

    @Override
    public void validateFacility(Facility facility) {
        ThrowUtils.throwIf(ObjectUtil.isNull(facility), ErrorCode.PARAMS_ERROR);
        String facilityName = facility.getFacilityName();
        ThrowUtils.throwIf(StrUtil.isBlank(facilityName), ErrorCode.PARAMS_ERROR, "设施名称不能为空");
    }

    @Override
    public QueryWrapper<Facility> getQueryWrapper(FacilityQueryRequest facilityQueryRequest) {
        if (facilityQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String facilityName = facilityQueryRequest.getFacilityName();
        String location = facilityQueryRequest.getLocation();
        String sortField = facilityQueryRequest.getSortField();
        String sortOrder = facilityQueryRequest.getSortOrder();

        QueryWrapper<Facility> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(facilityName), "facilityName", facilityName);
        queryWrapper.like(StrUtil.isNotBlank(location), "location", location);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Boolean existsWithField(String fieldName, Object value) {
        QueryWrapper<Facility> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public Facility getByFiled(String fieldName, Object value) {
        QueryWrapper<Facility> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.selectOne(queryWrapper);
    }
}




