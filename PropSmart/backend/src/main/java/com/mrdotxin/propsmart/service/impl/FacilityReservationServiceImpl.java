package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.model.dto.facility.reservation.FacilityReservationQueryRequest;
import com.mrdotxin.propsmart.model.entity.Facility;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.mapper.FacilityReservationMapper;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import com.mrdotxin.propsmart.service.FacilityService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【facilityreservation(设施预订)】的数据库操作Service实现
 * @createDate 2025-06-03 21:37:35
 */
@Service
public class FacilityReservationServiceImpl extends ServiceImpl<FacilityReservationMapper, FacilityReservation>
        implements FacilityReservationService {

    @Resource
    private FacilityService facilityService;

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

    @Override
    @AuthCheck(mustOwner=true)
    public long addReservation(FacilityReservation facilityReservation, Long userId) {
        if (facilityReservation == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验设施ID
        Integer facilityId = facilityReservation.getFacilityId();
        if (facilityId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "设施ID不能为空");
        }

        // 校验设施是否存在
        Facility facility = facilityService.getById(facilityId);
        if (facility == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "设施不存在");
        }

        // 校验预订时间
        Date reservationTime = facilityReservation.getReservationTime();
        if (reservationTime == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "预订时间不能为空");
        }

        // 校验时长
        Integer duration = facilityReservation.getDuration();
        if (duration == null || duration <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "预订时长不合法");
        }

        // 检查设施是否可预订
        boolean isAvailable = checkFacilityAvailability(facilityId, reservationTime, duration);
        if (!isAvailable) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该时段设施已被预订满");
        }

        // 设置初始状态和用户ID
        facilityReservation.setUserId(userId);
        facilityReservation.setStatus("pending");

        boolean result = this.save(facilityReservation);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        return facilityReservation.getId();
    }

    @Override
    @AuthCheck(mustRole= UserConstant.ADMIN_ROLE)
    public boolean reviewReservation(FacilityReservation facilityReservation, Long reviewerId) {
        if (facilityReservation == null || facilityReservation.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验状态
        String status = facilityReservation.getStatus().toString();
        if (StringUtils.isBlank(status) || (!status.equals("success") && !status.equals("rejected"))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态错误");
        }

        // 获取原记录
        FacilityReservation oldRecord = this.getById(facilityReservation.getId());
        if (oldRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 只能审批待处理的记录
        if (!oldRecord.getStatus().equals("pending")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该预订申请已处理");
        }

        // 如果是通过申请，需要再次检查容量
        if (status.equals("success")) {
            boolean isAvailable = checkFacilityAvailability(oldRecord.getFacilityId(),
                    oldRecord.getReservationTime(), oldRecord.getDuration());
            if (!isAvailable) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该时段设施已被预订满");
            }
        }

        // 设置审批信息
        facilityReservation.setReviewerId(reviewerId);
        facilityReservation.setReviewTime(new Date());

        return this.updateById(facilityReservation);
    }

    @Override
    public boolean checkFacilityAvailability(Integer facilityId, Date reservationTime, Integer duration) {
        // 参数校验
        if (facilityId == null || reservationTime == null || duration == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取设施信息和容量
        Facility facility = facilityService.getById(facilityId);
        if (facility == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "设施不存在");
        }

        Integer capacity = facility.getCapacity();
        if (capacity == null || capacity <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "设施容量配置错误");
        }

        // 计算预订结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reservationTime);
        calendar.add(Calendar.HOUR, duration);
        Date endTime = calendar.getTime();

        // 构建子查询：统计每个时间点的同时预订人数
        String subQuery = "(SELECT COUNT(*) " +
                "FROM facilityReservation t2 " +
                "WHERE t2.facilityId = t1.facilityId " +
                "AND t2.status IN ('pending', 'success') " +
                "AND t2.reservationTime < DATE_ADD(t1.reservationTime, INTERVAL t1.duration HOUR) " +
                "AND DATE_ADD(t2.reservationTime, INTERVAL t2.duration HOUR) > t1.reservationTime)";

        // 使用QueryWrapper构建查询
        QueryWrapper<FacilityReservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("MAX(" + subQuery + ") AS max_people")
                .eq("facilityId", facilityId)
                .in("status", "pending", "success")
                .and(wrapper -> wrapper
                        .between("reservationTime", reservationTime, endTime)
                        .or()
                        .apply("DATE_ADD(reservationTime, INTERVAL duration HOUR) > {0} AND reservationTime < {1}", reservationTime, endTime));

        // 执行查询获取最高人数
        List<Object> results = this.getBaseMapper().selectObjs(queryWrapper);

        // 处理查询结果，防止空指针异常
        Integer maxConcurrentPeople = 0;
        if (results != null && !results.isEmpty() && results.get(0) != null) {
            maxConcurrentPeople = Integer.valueOf(results.get(0).toString());
        }

        // 判断是否超过容量
        return maxConcurrentPeople < capacity;
    }

    @Override
    public QueryWrapper<FacilityReservation> getQueryWrapper(FacilityReservationQueryRequest facilityReservationQueryRequest) {
        if (facilityReservationQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        QueryWrapper<FacilityReservation> queryWrapper = new QueryWrapper<>();

        Integer facilityId = facilityReservationQueryRequest.getFacilityId();
        Long userId = facilityReservationQueryRequest.getUserId();
        String status = facilityReservationQueryRequest.getStatus();
        Date reservationTime = facilityReservationQueryRequest.getReservationTime();
        String sortField = facilityReservationQueryRequest.getSortField();
        String sortOrder = facilityReservationQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(facilityId != null,"facilityId", facilityId);
        queryWrapper.eq(userId != null,"userId", userId);
        queryWrapper.eq(StringUtils.isNotBlank(status),"status", status);
        queryWrapper.ge(reservationTime != null,"reservationTime", reservationTime);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }
}




