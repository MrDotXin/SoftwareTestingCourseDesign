package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionQueryRequest;
import com.mrdotxin.propsmart.model.entity.ComplaintSuggestion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 32054
* @description 针对表【complaintSuggestion(投诉建议)】的数据库操作Service
* @createDate 2025-06-04 10:23:23
*/
public interface ComplaintSuggestionService extends IService<ComplaintSuggestion> {

    /**
     * 添加投诉建议
     * @param complaintSuggestion
     * @param userId
     * @return
     */
    long addComplaint(ComplaintSuggestion complaintSuggestion, Long userId);

    /**
     * 处理投诉建议
     * @param complaintSuggestion
     * @param reviewerId
     * @return
     */
    boolean reviewComplaint(ComplaintSuggestion complaintSuggestion, Long reviewerId);

    /**
     * 获取查询条件
     * @param complaintQueryRequest
     * @return
     */
    QueryWrapper<ComplaintSuggestion> getQueryWrapper(ComplaintSuggestionQueryRequest complaintQueryRequest);
}
