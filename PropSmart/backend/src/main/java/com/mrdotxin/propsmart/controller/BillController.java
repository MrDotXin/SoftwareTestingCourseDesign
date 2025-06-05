package com.mrdotxin.propsmart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.DeleteRequest;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.model.dto.bill.BillAddRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillPayRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillQueryRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Bill;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.BillStatusEnum;
import com.mrdotxin.propsmart.model.enums.BillTypeEnum;
import com.mrdotxin.propsmart.model.vo.BillVO;
import com.mrdotxin.propsmart.service.BillService;
import com.mrdotxin.propsmart.websocket.NotificationService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账单接口
 */
@RestController
@RequestMapping("/bill")
@Api(tags = "账单接口")
@Slf4j
public class BillController {
    
    @Resource
    private BillService billService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private PropertyService propertyService;
    
    @Resource
    private NotificationService notificationService;
    
    /**
     * 创建账单
     *
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "创建账单")
    public BaseResponse<Long> addBill(@RequestBody BillAddRequest billAddRequest, HttpServletRequest request) {
        if (billAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long billId = billService.addBill(billAddRequest, request);
        
        // 获取创建的账单并发送通知
        Bill bill = billService.getById(billId);
        if (bill != null) {
            notificationService.handleBillNotification(bill, true);
        }
        
        return ResultUtils.success(billId);
    }
    
    /**
     * 删除账单
     *
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "删除账单")
    public BaseResponse<Boolean> deleteBill(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = billService.deleteBill(deleteRequest.getId(), request);
        return ResultUtils.success(result);
    }
    
    /**
     * 更新账单
     *
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新账单")
    public BaseResponse<Boolean> updateBill(@RequestBody BillUpdateRequest billUpdateRequest, HttpServletRequest request) {
        if (billUpdateRequest == null || billUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = billService.updateBill(billUpdateRequest, request);
        return ResultUtils.success(result);
    }
    
    /**
     * 根据ID获取账单
     *
     */
    @GetMapping("/get")
    @ApiOperation(value = "根据ID获取账单")
    public BaseResponse<BillVO> getBillById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        
        // 获取账单
        BillVO billVO = billService.getBillById(id);
        
        // 权限校验：只有管理员或账单所有者可以查看账单
        if (!userService.isAdmin(loginUser) && lacksBillAccess(loginUser, billVO)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return ResultUtils.success(billVO);
    }
    
    /**
     * 获取账单分页（管理员可查看所有账单）
     *
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取账单分页（管理员可查看所有账单）")
    public BaseResponse<Page<BillVO>> listBillByPage(@RequestBody BillQueryRequest billQueryRequest) {
        if (billQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<BillVO> billVOPage = billService.listBillByPage(billQueryRequest);
        return ResultUtils.success(billVOPage);
    }
    
    /**
     * 支付账单
     *
     */
    @PostMapping("/pay")
    @ApiOperation(value = "支付账单")
    public BaseResponse<Boolean> payBill(@RequestBody BillPayRequest billPayRequest, HttpServletRequest request) {
        if (billPayRequest == null || billPayRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        
        // 获取账单
        BillVO billVO = billService.getBillById(billPayRequest.getId());
        
        // 权限校验：只有管理员或账单所有者可以支付账单
        if (!userService.isAdmin(loginUser) && lacksBillAccess(loginUser, billVO)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "您无权支付该账单");
        }
        
        boolean result = billService.payBill(billPayRequest, request);
        
        // 支付成功后，获取更新后的账单并发送通知
        if (result) {
            Bill bill = billService.getById(billPayRequest.getId());
            if (bill != null) {
                notificationService.handleBillNotification(bill, false);
            }
        }
        
        return ResultUtils.success(result);
    }
    
    /**
     * 获取账单类型列表
     *
     */
    @GetMapping("/types")
    @ApiOperation(value = "获取账单类型列表")
    public BaseResponse<List<String>> getBillTypes() {
        List<String> types = BillTypeEnum.getTexts();
        return ResultUtils.success(types);
    }
    
    /**
     * 获取账单状态列表
     *
     */
    @GetMapping("/statuses")
    @ApiOperation(value = "获取账单状态列表")
    public BaseResponse<List<String>> getBillStatuses() {
        List<String> statuses = BillStatusEnum.getTexts();
        return ResultUtils.success(statuses);
    }
    
    /**
     * 获取我的账单
     *
     */
    @PostMapping("/my/list/page")
    @ApiOperation(value = "获取我的账单")
    public BaseResponse<Page<BillVO>> listMyBillByPage(@RequestBody BillQueryRequest billQueryRequest, HttpServletRequest request) {
        if (billQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        
        // 非业主无法查看账单
        if (!loginUser.getIsOwner() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非业主无法查看账单");
        }
        
        // 如果是管理员，可以查看所有账单
        if (userService.isAdmin(loginUser)) {
            Page<BillVO> billVOPage = billService.listBillByPage(billQueryRequest);
            return ResultUtils.success(billVOPage);
        }
        
        // 如果是业主，只能查看自己的账单
        // 获取该业主的所有房产ID
        List<Long> propertyIds = getUserPropertyIds(loginUser);
        
        if (propertyIds.isEmpty()) {
            // 如果没有房产，返回空页面
            Page<BillVO> emptyPage = new Page<>(billQueryRequest.getCurrent(), billQueryRequest.getPageSize(), 0);
            emptyPage.setRecords(new ArrayList<>());
            return ResultUtils.success(emptyPage);
        }
        
        // 设置查询条件，只查询业主的房产账单
        billQueryRequest.setPropertyId(null); // 清除可能的属性ID过滤
        Page<BillVO> billVOPage = billService.listBillByPage(billQueryRequest);
        
        // 过滤结果，只保留业主的房产账单
        List<BillVO> filteredRecords = billVOPage.getRecords().stream()
                .filter(billVO -> propertyIds.contains(billVO.getPropertyId()))
                .collect(Collectors.toList());
        
        // 创建新的分页对象
        Page<BillVO> filteredPage = new Page<>(billQueryRequest.getCurrent(), billQueryRequest.getPageSize(), filteredRecords.size());
        filteredPage.setRecords(filteredRecords);
        
        return ResultUtils.success(filteredPage);
    }
    
    /**
     * 判断用户是否缺少账单访问权限
     *
     * @param user 用户
     * @param billVO 账单
     * @return 如果用户无权访问该账单则返回true，否则返回false
     */
    private boolean lacksBillAccess(User user, BillVO billVO) {
        // 管理员可以查看所有账单
        if (userService.isAdmin(user)) {
            return false;
        }
        
        // 非业主无法查看账单
        if (!user.getIsOwner()) {
            return true;
        }
        
        // 业主只能查看自己的账单
        List<Long> propertyIds = getUserPropertyIds(user);
        return !propertyIds.contains(billVO.getPropertyId());
    }
    
    /**
     * 获取用户的房产ID列表
     *
     * @param user 用户
     * @return 房产ID列表
     */
    private List<Long> getUserPropertyIds(User user) {
        // 通过用户的身份证号查询所有属于该用户的房产
        if (user == null || user.getUserIdCardNumber() == null) {
            return new ArrayList<>();
        }
        
        // 查询条件：ownerIdentity等于用户的身份证号
        List<Property> properties = propertyService.listByField("ownerIdentity", user.getUserIdCardNumber());
        
        // 提取房产ID列表
        return properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
    }
} 