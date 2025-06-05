package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionQueryRequest;
import com.mrdotxin.propsmart.model.entity.ComplaintSuggestion;
import com.mrdotxin.propsmart.service.ComplaintSuggestionService;
import com.mrdotxin.propsmart.mapper.ComplaintSuggestionMapper;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 32054
* @description 针对表【complaintSuggestion(投诉建议)】的数据库操作Service实现
* @createDate 2025-06-04 10:23:23
*/
@Service
public class ComplaintSuggestionServiceImpl extends ServiceImpl<ComplaintSuggestionMapper, ComplaintSuggestion>
        implements ComplaintSuggestionService {

    @Override
    public long addComplaint(ComplaintSuggestion complaintSuggestion, Long userId) {
        if (complaintSuggestion == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验内容
        String content = complaintSuggestion.getContent();
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容不能为空");
        }

        // 校验类型
        String type = complaintSuggestion.getType().toString();
        if (StringUtils.isBlank(type) || (!type.equals("complaint") && !type.equals("suggestion"))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "类型错误");
        }

        // 设置初始状态和用户ID
        complaintSuggestion.setUserId(userId);
        complaintSuggestion.setStatus("pending");
        complaintSuggestion.setCreateTime(new Date());

        boolean result = this.save(complaintSuggestion);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }

        return complaintSuggestion.getId();
    }

    @Override
    public boolean reviewComplaint(ComplaintSuggestion complaintSuggestion, Long reviewerId) {
        if (complaintSuggestion == null || complaintSuggestion.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验状态
        String status = complaintSuggestion.getStatus().toString();
        if (StringUtils.isBlank(status) || (!status.equals("success") && !status.equals("rejected"))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态错误");
        }

        // 获取原记录
        ComplaintSuggestion oldRecord = this.getById(complaintSuggestion.getId());
        if (oldRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 只能审批待处理的记录
        if (!oldRecord.getStatus().equals("pending")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该投诉建议已处理");
        }

        // 设置审批信息
        complaintSuggestion.setReviewerId(reviewerId);
        complaintSuggestion.setReviewTime(new Date());

        return this.updateById(complaintSuggestion);
    }

    @Override
    public QueryWrapper<ComplaintSuggestion> getQueryWrapper(ComplaintSuggestionQueryRequest complaintQueryRequest) {
        if (complaintQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        QueryWrapper<ComplaintSuggestion> queryWrapper = new QueryWrapper<>();

        String content = complaintQueryRequest.getContent();
        String type = complaintQueryRequest.getType();
        String status = complaintQueryRequest.getStatus();
        Long userId = complaintQueryRequest.getUserId();
        Long reviewerId = complaintQueryRequest.getReviewerId();
        String sortField = complaintQueryRequest.getSortField();
        String sortOrder = complaintQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(content),"content", content);
        queryWrapper.eq(StringUtils.isNotBlank(type),"type", type);
        queryWrapper.eq(StringUtils.isNotBlank(status),"status", status);
        queryWrapper.eq(userId != null,"userId", userId);
        queryWrapper.eq(reviewerId != null,"reviewerId", reviewerId);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

}




