package com.mrdotxin.propsmart.service.impl;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.mysql.VisitorMapper;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorQueryRequest;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.entity.Visitor;
import com.mrdotxin.propsmart.model.enums.VisitorReviewStatusEnum;
import com.mrdotxin.propsmart.service.VisitorService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 访客管理服务实现
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Value("${jwt-secret}")
    private String jwtSecret;

    @Override
    public long addVisitor(Visitor visitor, Long userId) {
        if (visitor == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验访客姓名
        String visitorName = visitor.getVisitorName();
        if (StringUtils.isBlank(visitorName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "访客姓名不能为空");
        }

        // 校验访问时间
        Date visitTime = visitor.getVisitTime();
        if (visitTime == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "访问时间不能为空");
        }

        // 校验时长
        Date duration = visitor.getVisitEndTime();
        if (duration == null || !duration.after(visitTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "访问结束时间不合法");
        }

        ThrowUtils.throwIf(existContradictionVisit(visitor.getIdNumber(), visitor.getVisitTime(), visitor.getVisitEndTime()), ErrorCode.OPERATION_ERROR, "当前预约时间有冲突!");

        // 设置初始状态和用户ID
        visitor.setUserId(userId);
        visitor.setReviewStatus("pending");

        boolean result = this.save(visitor);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        return visitor.getId();
    }

    @Override
    public boolean reviewVisitor(Visitor visitor, Long reviewerId) {
        if (visitor == null || visitor.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验状态
        String reviewStatus = visitor.getReviewStatus();
        if (StringUtils.isBlank(reviewStatus) || (!reviewStatus.equals("approved") && !reviewStatus.equals("rejected"))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态错误");
        }

        // 设置审批信息
        visitor.setReviewerId(reviewerId);
        visitor.setReviewTime(new Date());

        // 如果审批通过，生成电子通行证
        if (reviewStatus.equals(VisitorReviewStatusEnum.APPROVED.getValue())) {
            String passCode = generatePassCode(visitor.getIdNumber(), visitor.getVisitTime(), visitor.getVisitEndTime());
            visitor.setPassCode(passCode);
        }

        return this.updateById(visitor);
    }

    @Override
    public String generatePassCode(String idCardNumber, Date visitTime, Date visitEndTime) {
        long expirationTime = visitEndTime.getTime() - visitTime.getTime();

        Map<String, Object> payload = new HashMap<>();
        payload.put("idCardNumber", idCardNumber);
        payload.put("iat", visitTime);
        payload.put("exp", expirationTime);

        // 生成JWT
        return JWTUtil.createToken(payload, jwtSecret.getBytes());
    }


    @Override
    public String validatePassCode(String token, User loginUser) {
        try {
            ThrowUtils.throwIf(!JWTUtil.verify(token, jwtSecret.getBytes()), ErrorCode.PARAMS_ERROR, "当前不在允许访问的时间内, token无效");
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "错误! 当前授权码格式有错误!");
        }
        JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload("idCardNumber");
    }

    @Override
    public boolean existContradictionVisit(String identity, Date beginTime, Date endTime) {
        QueryWrapper<Visitor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("idNumber", identity);
        queryWrapper.and(wrapper ->
            wrapper.lt("visitTime", endTime)        // 记录开始时间 ≤ 查询结束时间
                   .and(nested ->                   // 并且
                       nested.gt("visitEndTime", beginTime)  // 记录结束时间 ≥ 查询开始时间
                   )
        );

        return this.baseMapper.exists(queryWrapper);
    }


    @Override
    public QueryWrapper<Visitor> getQueryWrapper(VisitorQueryRequest visitorQueryRequest) {
        if (visitorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        QueryWrapper<Visitor> queryWrapper = new QueryWrapper<>();

        String visitorName = visitorQueryRequest.getVisitorName();
        String idNumber = visitorQueryRequest.getIdNumber();
        Long userId = visitorQueryRequest.getUserId();
        String reviewStatus = visitorQueryRequest.getReviewStatus();
        Date visitTime = visitorQueryRequest.getVisitTime();
        String sortField = visitorQueryRequest.getSortField();
        String sortOrder = visitorQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(visitorName), "visitorName", visitorName);
        queryWrapper.eq(StringUtils.isNotBlank(idNumber), "idNumber", idNumber);
        queryWrapper.eq(StringUtils.isNotBlank(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.ge(visitTime != null,"visitTime", visitTime);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }
} 