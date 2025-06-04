package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.mapper.VisitorMapper;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorQueryRequest;
import com.mrdotxin.propsmart.model.entity.Visitor;
import com.mrdotxin.propsmart.service.VisitorService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 访客管理服务实现
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

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
        if (!oldRecord.getReviewStatus().equals("pending")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该访客申请已处理");
        }
        
        // 设置审批信息
        visitor.setReviewerId(reviewerId);
        visitor.setReviewTime(new Date());
        
        // 如果审批通过，生成电子通行证
        if (reviewStatus.equals("approved")) {
            String passCode = generatePassCode(visitor.getId());
            visitor.setPassCode(passCode);
        }
        
        return this.updateById(visitor);
    }

    @Override
    public String generatePassCode(Long visitorId) {
        if (visitorId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 生成8位随机字母数字组合的通行证
        String randomCode = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        
        // 加上访客ID前缀，确保唯一性
        return "V" + visitorId + "-" + randomCode;
    }

    @Override
    public QueryWrapper<Visitor> getQueryWrapper(VisitorQueryRequest visitorQueryRequest) {
        if (visitorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        
        QueryWrapper<Visitor> queryWrapper = new QueryWrapper<>();
        
        String visitorName = visitorQueryRequest.getVisitorName();
        String idNumber = visitorQueryRequest.getIdNumber();
        String reviewStatus = visitorQueryRequest.getReviewStatus();
        Long userId = visitorQueryRequest.getUserId();
        Date visitTime = visitorQueryRequest.getVisitTime();
        
        // 拼接查询条件
        if (StringUtils.isNotBlank(visitorName)) {
            queryWrapper.like("visitorName", visitorName);
        }
        
        if (StringUtils.isNotBlank(idNumber)) {
            queryWrapper.eq("idNumber", idNumber);
        }
        
        if (StringUtils.isNotBlank(reviewStatus)) {
            queryWrapper.eq("reviewStatus", reviewStatus);
        }
        
        if (userId != null) {
            queryWrapper.eq("userId", userId);
        }
        
        if (visitTime != null) {
            queryWrapper.ge("visitTime", visitTime);
        }
        
        queryWrapper.orderByDesc("createTime");
        
        return queryWrapper;
    }
} 