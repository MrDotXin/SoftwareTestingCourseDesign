package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionQueryRequest;
import com.mrdotxin.propsmart.model.entity.ComplaintSuggestion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* 投诉建议服务
*/
public interface ComplaintSuggestionService extends IService<ComplaintSuggestion> {

    /**
     * 添加投诉建议
     */
    long addComplaint(ComplaintSuggestion complaintSuggestion, Long userId);

    /**
     * 处理投诉建议
     */
    boolean reviewComplaint(ComplaintSuggestion complaintSuggestion, Long reviewerId);

    /**
     * 获取查询条件
     */
    QueryWrapper<ComplaintSuggestion> getQueryWrapper(ComplaintSuggestionQueryRequest complaintQueryRequest);
}
