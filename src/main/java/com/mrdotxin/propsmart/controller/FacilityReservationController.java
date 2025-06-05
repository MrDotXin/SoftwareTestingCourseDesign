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
import com.mrdotxin.propsmart.model.dto.facility.reservation.FacilityReservationAddRequest;
import com.mrdotxin.propsmart.model.dto.facility.reservation.FacilityReservationQueryRequest;
import com.mrdotxin.propsmart.model.dto.facility.reservation.FacilityReservationUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Facility;
import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import com.mrdotxin.propsmart.service.FacilityService;
import com.mrdotxin.propsmart.websocket.NotificationService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 设施预约管理接口
 */
@RestController
@RequestMapping("/facility/reservation")
@Api(tags = "设施预约管理")
@Slf4j
public class FacilityReservationController {

    @Resource
    private FacilityReservationService facilityReservationService;

    @Resource
    private FacilityService facilityService;

    @Resource
    private UserService userService;
    
    @Resource
    private NotificationService notificationService;

    /**
     * 提交设施预约申请
     */
    @PostMapping("/submit")
    @ApiOperation(value = "提交设施预约申请", notes = "用户提交设施预约申请")
    public BaseResponse<Long> submitReservation(@RequestBody FacilityReservationAddRequest reservationAddRequest,
                                               HttpServletRequest request) {
        if (reservationAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);

        // 验证设施是否存在
        Integer facilityId = reservationAddRequest.getFacilityId();
        Facility facility = facilityService.getById(facilityId);
        ThrowUtils.throwIf(facility == null, ErrorCode.NOT_FOUND_ERROR, "设施不存在");

        // 创建预约对象
        FacilityReservation facilityReservation = new FacilityReservation();
        BeanUtils.copyProperties(reservationAddRequest, facilityReservation);

        long result = facilityReservationService.addReservation(facilityReservation, loginUser.getId());
        
        // 获取创建的预约并发送通知
        FacilityReservation savedReservation = facilityReservationService.getById(result);
        if (savedReservation != null) {
            notificationService.handleFacilityReservationNotification(savedReservation, true);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 处理设施预约（管理员审批）
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "审批设施预约", notes = "管理员审批设施预约申请")
    public BaseResponse<Boolean> reviewReservation(@RequestBody FacilityReservationUpdateRequest reservationUpdateRequest,
                                                  HttpServletRequest request) {
        if (reservationUpdateRequest == null || reservationUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        
        // 仅支持的状态：success 或 rejected
        String status = reservationUpdateRequest.getStatus();
        if (!"success".equals(status) && !"rejected".equals(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态只能是 'success' 或 'rejected'");
        }

        FacilityReservation facilityReservation = new FacilityReservation();
        BeanUtils.copyProperties(reservationUpdateRequest, facilityReservation);
        
        boolean result = facilityReservationService.reviewReservation(facilityReservation, loginUser.getId());
        
        // 获取更新后的预约并发送通知
        if (result) {
            FacilityReservation updatedReservation = facilityReservationService.getById(reservationUpdateRequest.getId());
            if (updatedReservation != null) {
                notificationService.handleFacilityReservationNotification(updatedReservation, false);
            }
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 取消预约申请（用户）
     */
    @PostMapping("/cancel")
    @ApiOperation(value = "取消预约申请", notes = "用户取消自己的预约申请")
    public BaseResponse<Boolean> cancelReservation(@RequestBody DeleteRequest deleteRequest,
                                                 HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 校验是否存在
        FacilityReservation oldReservation = facilityReservationService.getById(id);
        ThrowUtils.throwIf(oldReservation == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可取消
        if (!oldReservation.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只能取消自己的预约");
        }
        
        // 如果已经审批通过，不能直接取消
        if ("success".equals(oldReservation.getStatus())) {
            Date now = new Date();
            if (now.after(oldReservation.getReservationTime())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "已通过的预约且已过预约时间，无法取消");
            }
        }
        
        boolean result = facilityReservationService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取预约详情
     */
    @GetMapping("/get")
    @ApiOperation(value = "获取预约详情", notes = "根据ID获取预约详情，仅本人或管理员可查看")
    public BaseResponse<FacilityReservation> getReservationById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        FacilityReservation reservation = facilityReservationService.getById(id);
        if (reservation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可查看详情
        if (!reservation.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return ResultUtils.success(reservation);
    }

    /**
     * 分页获取预约列表（仅管理员）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取预约列表", notes = "管理员分页获取所有预约记录")
    public BaseResponse<Page<FacilityReservation>> listReservationByPage(@RequestBody FacilityReservationQueryRequest reservationQueryRequest,
                                                                       HttpServletRequest request) {
        if (reservationQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = reservationQueryRequest.getCurrent();
        long size = reservationQueryRequest.getPageSize();
        
        Page<FacilityReservation> reservationPage = facilityReservationService.page(new Page<>(current, size),
                facilityReservationService.getQueryWrapper(reservationQueryRequest));
        
        return ResultUtils.success(reservationPage);
    }

    /**
     * 分页获取当前用户的预约列表
     */
    @PostMapping("/my/list/page")
    @ApiOperation(value = "分页获取我的预约", notes = "分页获取当前用户的预约列表")
    public BaseResponse<Page<FacilityReservation>> listMyReservationByPage(@RequestBody FacilityReservationQueryRequest reservationQueryRequest,
                                                                         HttpServletRequest request) {
        if (reservationQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        reservationQueryRequest.setUserId(loginUser.getId());
        
        long current = reservationQueryRequest.getCurrent();
        long size = reservationQueryRequest.getPageSize();
        
        Page<FacilityReservation> reservationPage = facilityReservationService.page(new Page<>(current, size),
                facilityReservationService.getQueryWrapper(reservationQueryRequest));
        
        return ResultUtils.success(reservationPage);
    }
    
    /**
     * 检查设施在特定时间是否可预约
     */
    @GetMapping("/check-availability")
    @ApiOperation(value = "检查设施可用性", notes = "检查设施在特定时间段是否可以预约")
    public BaseResponse<Boolean> checkAvailability(@RequestParam Integer facilityId,
                                                 @RequestParam Long reservationTime,
                                                 @RequestParam Integer duration) {
        if (facilityId == null || facilityId <= 0 || reservationTime == null || duration == null || duration <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 验证设施是否存在
        Facility facility = facilityService.getById(facilityId);
        ThrowUtils.throwIf(facility == null, ErrorCode.NOT_FOUND_ERROR, "设施不存在");
        
        Date bookingTime = new Date(reservationTime);
        boolean isAvailable = facilityReservationService.checkFacilityAvailability(facilityId, bookingTime, duration);
        
        return ResultUtils.success(isAvailable);
    }
    
    /**
     * 获取预约状态统计数据（管理员）
     */
    @GetMapping("/stats")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取预约统计", notes = "获取预约状态统计数据")
    public BaseResponse<Map<String, Long>> getReservationStats() {
        // 统计不同状态的预约数量
        Map<String, Long> stats = new HashMap<>();
        long pendingCount = facilityReservationService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FacilityReservation>()
                .eq(FacilityReservation::getStatus, "pending"));
        long successCount = facilityReservationService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FacilityReservation>()
                .eq(FacilityReservation::getStatus, "success"));
        long rejectedCount = facilityReservationService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FacilityReservation>()
                .eq(FacilityReservation::getStatus, "rejected"));
        long totalCount = facilityReservationService.count();
        
        stats.put("pending", pendingCount);
        stats.put("success", successCount);
        stats.put("rejected", rejectedCount);
        stats.put("total", totalCount);
        
        return ResultUtils.success(stats);
    }
} 