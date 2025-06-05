package com.mrdotxin.propsmart.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.notice.NoticeAddRequest;
import com.mrdotxin.propsmart.model.dto.notice.NoticeQueryRequest;
import com.mrdotxin.propsmart.model.dto.notice.NoticeUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Notice;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.NoticeService;
import com.mrdotxin.propsmart.websocket.NotificationService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/notice")
@Api(tags = "公告管理")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private UserService userService;
    
    @Resource
    private NotificationService notificationService;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    @ApiOperation(value = "添加公告")
    public BaseResponse<Long> addNotice(@RequestBody NoticeAddRequest noticeAddRequest,
                                        HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(noticeAddRequest), ErrorCode.PARAMS_ERROR);

        User loginUser = userService.getLoginUser(request);
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeAddRequest, notice);
        notice.setPublisherId(loginUser.getId());

        noticeService.validateNotice(notice);
        boolean result = noticeService.save(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");
        
        // 发送WebSocket通知
        notificationService.handleNoticeNotification(notice);

        return ResultUtils.success(notice.getId());
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    @ApiOperation(value = "更新公告")
    public BaseResponse<Boolean> updateNotice(@RequestBody NoticeUpdateRequest noticeUpdateRequest,
                                             HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isNull(noticeUpdateRequest), ErrorCode.PARAMS_ERROR);

        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeUpdateRequest, notice);
        noticeService.validateNotice(notice);

        boolean result = noticeService.updateById(notice);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    @ApiOperation(value = "删除公告")
    public BaseResponse<Boolean> deleteNotice(@RequestBody DeleteRequest deleteRequest,
                                             HttpServletRequest request) {
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        boolean result = noticeService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    @ApiOperation(value = "根据ID获取公告详情")
    public BaseResponse<Notice> getNoticeById(@RequestParam Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Notice notice = noticeService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(notice), ErrorCode.NOT_FOUND_ERROR, "公告不存在");
        return ResultUtils.success(notice);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取公告列表（管理员）")
    public BaseResponse<Page<Notice>> listNoticeByPage(@RequestBody NoticeQueryRequest noticeQueryRequest,
                                                       HttpServletRequest request) {
        long current = noticeQueryRequest.getCurrent();
        long size = noticeQueryRequest.getPageSize();
        Page<Notice> noticePage = noticeService.page(new Page<>(current, size),
                noticeService.getQueryWrapper(noticeQueryRequest));
        return ResultUtils.success(noticePage);
    }

    @GetMapping("/list/public")
    @ApiOperation(value = "分页获取有效公告（公开接口）")
    public BaseResponse<Page<Notice>> listPublicNotice(@RequestBody NoticeQueryRequest noticeQueryRequest,
                                                       HttpServletRequest request) {
        long current = noticeQueryRequest.getCurrent();
        long size = noticeQueryRequest.getPageSize();

        // 只查询未过期的公告
        QueryWrapper<Notice> queryWrapper = noticeService.getQueryWrapper(noticeQueryRequest);
        queryWrapper.le("publishTime", new Date()); // 发布时间小于等于当前时间
        queryWrapper.and(wrapper ->
            wrapper.isNull("expireTime")
                .or().ge("expireTime", new Date())
        );

        Page<Notice> noticePage = noticeService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(noticePage);
    }
}
