package com.mrdotxin.propsmart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionAddRequest;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionQueryRequest;
import com.mrdotxin.propsmart.model.dto.complaint.ComplaintSuggestionUpdateRequest;
import com.mrdotxin.propsmart.model.entity.ComplaintSuggestion;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.ComplaintSuggestionService;
import com.mrdotxin.propsmart.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 投诉建议接口
 */
@RestController
@RequestMapping("/complaintSuggestion")
@Slf4j
public class ComplaintSuggestionController {

    @Resource
    private ComplaintSuggestionService complaintService;

    @Resource
    private UserService userService;

    /**
     * 创建投诉建议
     *
     * @param complaintSuggestionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addComplaint(@RequestBody ComplaintSuggestionAddRequest complaintSuggestionAddRequest,
                                          HttpServletRequest request) {
        if (complaintSuggestionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        ComplaintSuggestion complaintSuggestion = new ComplaintSuggestion();
        BeanUtils.copyProperties(complaintSuggestionAddRequest, complaintSuggestion);
        
        long result = complaintService.addComplaint(complaintSuggestion, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除投诉建议
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComplaint(@RequestBody DeleteRequest deleteRequest,
                                               HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 校验是否存在
        ComplaintSuggestion oldComplaint = complaintService.getById(id);
        ThrowUtils.throwIf(oldComplaint == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldComplaint.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = complaintService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新投诉建议（管理员审批）
     *
     * @param complaintUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateComplaint(@RequestBody ComplaintSuggestionUpdateRequest complaintUpdateRequest,
                                              HttpServletRequest request) {
        if (complaintUpdateRequest == null || complaintUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);

        // 仅管理员可审批
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        ComplaintSuggestion complaintSuggestion = new ComplaintSuggestion();
        BeanUtils.copyProperties(complaintUpdateRequest, complaintSuggestion);
        
        boolean result = complaintService.reviewComplaint(complaintSuggestion, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取投诉建议
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ComplaintSuggestion> getComplaintById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        ComplaintSuggestion complaint = complaintService.getById(id);
        if (complaint == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可查看详情
        if (!complaint.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return ResultUtils.success(complaint);
    }

    /**
     * 分页获取投诉建议列表（仅管理员）
     *
     * @param complaintQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ComplaintSuggestion>> listComplaintByPage(@RequestBody ComplaintSuggestionQueryRequest complaintQueryRequest,
                                                                     HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 仅管理员
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        long current = complaintQueryRequest.getCurrent();
        long size = complaintQueryRequest.getPageSize();
        
        Page<ComplaintSuggestion> complaintPage = complaintService.page(new Page<>(current, size),
                complaintService.getQueryWrapper(complaintQueryRequest));
        
        return ResultUtils.success(complaintPage);
    }

    /**
     * 分页获取当前用户的投诉建议列表
     *
     * @param complaintQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<ComplaintSuggestion>> listMyComplaintByPage(@RequestBody ComplaintSuggestionQueryRequest complaintQueryRequest,
                                                                       HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        complaintQueryRequest.setUserId(loginUser.getId());
        
        long current = complaintQueryRequest.getCurrent();
        long size = complaintQueryRequest.getPageSize();
        
        Page<ComplaintSuggestion> complaintPage = complaintService.page(new Page<>(current, size),
                complaintService.getQueryWrapper(complaintQueryRequest));
        
        return ResultUtils.success(complaintPage);
    }
} 