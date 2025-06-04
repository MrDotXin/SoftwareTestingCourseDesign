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
import com.mrdotxin.propsmart.model.dto.visitor.VisitorAddRequest;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorQueryRequest;
import com.mrdotxin.propsmart.model.dto.visitor.VisitorUpdateRequest;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.entity.Visitor;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.service.VisitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 访客管理接口
 */
@RestController
@RequestMapping("/visitor")
@Slf4j
public class VisitorController {

    @Resource
    private VisitorService visitorService;

    @Resource
    private UserService userService;

    /**
     * 创建访客申请
     *
     * @param visitorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addVisitor(@RequestBody VisitorAddRequest visitorAddRequest,
                                        HttpServletRequest request) {
        if (visitorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        Visitor visitor = new Visitor();
        BeanUtils.copyProperties(visitorAddRequest, visitor);
        
        long result = visitorService.addVisitor(visitor, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除访客申请
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteVisitor(@RequestBody DeleteRequest deleteRequest,
                                             HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 校验是否存在
        Visitor oldVisitor = visitorService.getById(id);
        ThrowUtils.throwIf(oldVisitor == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldVisitor.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = visitorService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新访客申请（管理员审批）
     *
     * @param visitorUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateVisitor(@RequestBody VisitorUpdateRequest visitorUpdateRequest,
                                            HttpServletRequest request) {
        if (visitorUpdateRequest == null || visitorUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 仅管理员可审批
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        Visitor visitor = new Visitor();
        BeanUtils.copyProperties(visitorUpdateRequest, visitor);
        
        boolean result = visitorService.reviewVisitor(visitor, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取访客申请
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Visitor> getVisitorById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        Visitor visitor = visitorService.getById(id);
        if (visitor == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可查看详情
        if (!visitor.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return ResultUtils.success(visitor);
    }

    /**
     * 分页获取访客申请列表（仅管理员）
     *
     * @param visitorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Visitor>> listVisitorByPage(@RequestBody VisitorQueryRequest visitorQueryRequest,
                                                       HttpServletRequest request) {
        long current = visitorQueryRequest.getCurrent();
        long size = visitorQueryRequest.getPageSize();
        
        Page<Visitor> visitorPage = visitorService.page(new Page<>(current, size),
                visitorService.getQueryWrapper(visitorQueryRequest));
        
        return ResultUtils.success(visitorPage);
    }

    /**
     * 分页获取当前用户的访客申请列表
     *
     * @param visitorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Visitor>> listMyVisitorByPage(@RequestBody VisitorQueryRequest visitorQueryRequest,
                                                         HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        visitorQueryRequest.setUserId(loginUser.getId());
        
        long current = visitorQueryRequest.getCurrent();
        long size = visitorQueryRequest.getPageSize();
        
        Page<Visitor> visitorPage = visitorService.page(new Page<>(current, size),
                visitorService.getQueryWrapper(visitorQueryRequest));
        
        return ResultUtils.success(visitorPage);
    }
    
    /**
     * 获取电子通行证
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/passCode")
    public BaseResponse<String> getPassCode(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        Visitor visitor = visitorService.getById(id);
        if (visitor == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可查看通行证
        if (!visitor.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 检查是否已审批通过
        if (!"approved".equals(visitor.getReviewStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "访客申请未审批通过");
        }
        
        String passCode = visitor.getPassCode();
        if (passCode == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "通行证生成失败");
        }
        
        return ResultUtils.success(passCode);
    }
} 