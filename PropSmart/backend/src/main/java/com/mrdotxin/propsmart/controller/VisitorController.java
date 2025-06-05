package com.mrdotxin.propsmart.controller;

import cn.hutool.core.util.StrUtil;
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
import com.mrdotxin.propsmart.websocket.NotificationService;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.service.VisitorService;
import com.mrdotxin.propsmart.utils.FormatUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 访客管理接口
 */
@Slf4j
@Api(tags = "访客记录功能")
@RestController
@RequestMapping("/visitor")
public class VisitorController {

    @Resource
    private VisitorService visitorService;

    @Resource
    private UserService userService;
    
    @Resource
    private NotificationService notificationService;

    /**
     * 创建访客申请
     *
     */
    @PostMapping("/submit")
    @ApiOperation(value = "提交访问申请")
    public BaseResponse<Long> submitVisitRequest(@RequestBody VisitorAddRequest visitorAddRequest,
                                         HttpServletRequest request) {
        if (visitorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        Visitor visitor = new Visitor();
        if (!FormatUtils.isValidNameAndIdCard(visitor.getVisitorName(), visitor.getIdNumber())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "身份证和姓名验证失败!");
        }

        BeanUtils.copyProperties(visitorAddRequest, visitor);

        long result = visitorService.addVisitor(visitor, loginUser.getId());
        
        // 获取创建的访客申请并发送通知
        Visitor savedVisitor = visitorService.getById(result);
        if (savedVisitor != null) {
            notificationService.handleVisitorNotification(savedVisitor, true);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 删除访客申请
     *
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除访问申请")
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
    @ApiOperation(value = "管理员审核访问申请")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewVisitor(@RequestBody VisitorUpdateRequest visitorUpdateRequest,
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
        
        // 获取更新后的访客申请并发送通知
        if (result) {
            Visitor updatedVisitor = visitorService.getById(visitorUpdateRequest.getId());
            if (updatedVisitor != null) {
                notificationService.handleVisitorNotification(updatedVisitor, false);
            }
        }
        
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
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取申请详情")
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
    @ApiOperation(value = "分页获取申请")
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
     */
    @PostMapping("/my/list/page")
    @ApiOperation(value = "分页获取用户自身的申请")
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
    
    @PostMapping("/validate")
    @ApiOperation(value = "验证申请码是否有效")
    public BaseResponse<String> validateAssignedToken(@RequestParam String token, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(StrUtil.isBlank(token), ErrorCode.PARAMS_ERROR);

        User user = userService.getLoginUser(httpServletRequest);

        return ResultUtils.success(visitorService.validatePassCode(token, user));
    }
} 