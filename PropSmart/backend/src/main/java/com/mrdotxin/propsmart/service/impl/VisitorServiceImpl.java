package com.mrdotxin.propsmart.service.impl;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.VisitorMapper;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorQueryRequest;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.entity.Visitor;
import com.mrdotxin.propsmart.model.enums.VisitorReviewStatusEnum;
import com.mrdotxin.propsmart.service.VisitorService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.lang3.RandomStringUtils;
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
        Integer duration = visitor.getDuration();
        if (duration == null || duration <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "访问时长不合法");
        }

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

        // 获取原记录
        Visitor oldRecord = this.getById(visitor.getId());
        if (oldRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 只能审批待处理的记录
        if (!oldRecord.getReviewStatus().equals(VisitorReviewStatusEnum.PENDING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该访客申请已处理");
        }

        // 设置审批信息
        visitor.setReviewerId(reviewerId);
        visitor.setReviewTime(new Date());

        // 如果审批通过，生成电子通行证
        if (reviewStatus.equals(VisitorReviewStatusEnum.APPROVED.getValue())) {
            String passCode = generatePassCode(visitor.getIdNumber(), visitor.getVisitTime(), visitor.getDuration());
            visitor.setPassCode(passCode);
        }

        return this.updateById(visitor);
    }

    @Override
    public String generatePassCode(String idCardNumber, Date visitTime, Integer duration) {
        long expirationTime = visitTime.getTime() + duration * 1000 * 3600;

        Map<String, Object> payload = new HashMap<>();
        payload.put("idCardNumber", idCardNumber);
        payload.put("iat", visitTime);
        payload.put("exp", expirationTime);

        // 生成JWT
        return JWTUtil.createToken(payload, jwtSecret.getBytes());
    }


    @Override
    public String validatePassCode(String token, User loginUser) {
        ThrowUtils.throwIf(JWTUtil.verify(token, jwtSecret.getBytes()), ErrorCode.PARAMS_ERROR, "当前不在允许访问的时间内, token无效");

        JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload("idCardNumber");
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
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.ge(visitTime != null,"visitTime", visitTime);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }
} 