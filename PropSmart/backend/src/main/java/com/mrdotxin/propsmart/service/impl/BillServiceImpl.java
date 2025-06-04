package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.mapper.BillMapper;
import com.mrdotxin.propsmart.mapper.PropertyMapper;
import com.mrdotxin.propsmart.model.dto.bill.BillAddRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillPayRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillQueryRequest;
import com.mrdotxin.propsmart.model.dto.bill.BillUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Bill;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.BillStatusEnum;
import com.mrdotxin.propsmart.model.enums.BillTypeEnum;
import com.mrdotxin.propsmart.model.vo.BillVO;
import com.mrdotxin.propsmart.service.BillService;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 账单服务实现
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill>
    implements BillService {
    
    @Resource
    private UserService userService;
    
    @Resource
    private BuildingService buildingService;
    
    @Resource
    private PropertyMapper propertyMapper;

    @Override
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public long addBill(BillAddRequest billAddRequest, HttpServletRequest request) {
        if (billAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 检查房产是否存在
        Long propertyId = billAddRequest.getPropertyId();
        Property property = propertyMapper.selectById(propertyId);
        if (property == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "房产不存在");
        }
        
        // 检查账单类型
        String type = billAddRequest.getType();
        if (!BillTypeEnum.getValues().contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账单类型错误");
        }
        
        // 创建账单
        Bill bill = new Bill();
        BeanUtils.copyProperties(billAddRequest, bill);
        bill.setStatus(BillStatusEnum.UNPAID.getValue());
        
        boolean result = this.save(bill);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建账单失败");
        }
        
        return bill.getId();
    }

    @Override
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public boolean deleteBill(long id, HttpServletRequest request) {
        // 检查账单是否存在
        Bill bill = this.getById(id);
        if (bill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return this.removeById(id);
    }

    @Override
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public boolean updateBill(BillUpdateRequest billUpdateRequest, HttpServletRequest request) {
        if (billUpdateRequest == null || billUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 检查账单是否存在
        Bill oldBill = this.getById(billUpdateRequest.getId());
        if (oldBill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 如果更新房产ID，检查房产是否存在
        Long propertyId = billUpdateRequest.getPropertyId();
        if (propertyId != null && propertyId > 0) {
            Property property = propertyMapper.selectById(propertyId);
            if (property == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "房产不存在");
            }
        }
        
        // 检查账单类型
        String type = billUpdateRequest.getType();
        if (StringUtils.isNotBlank(type) && !BillTypeEnum.getValues().contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账单类型错误");
        }
        
        // 检查账单状态
        String status = billUpdateRequest.getStatus();
        if (StringUtils.isNotBlank(status) && !BillStatusEnum.getValues().contains(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账单状态错误");
        }
        
        // 更新账单
        Bill bill = new Bill();
        BeanUtils.copyProperties(billUpdateRequest, bill);
        
        return this.updateById(bill);
    }

    @Override
    public BillVO getBillById(long id) {
        Bill bill = this.getById(id);
        if (bill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return getBillVO(bill);
    }

    @Override
    public Page<BillVO> listBillByPage(BillQueryRequest billQueryRequest) {
        if (billQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = billQueryRequest.getCurrent();
        long size = billQueryRequest.getPageSize();
        
        // 构建查询条件
        QueryWrapper<Bill> queryWrapper = this.getQueryWrapper(billQueryRequest);
        
        // 分页查询
        Page<Bill> billPage = this.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<BillVO> billVOPage = new Page<>(current, size, billPage.getTotal());
        
        // 填充VO数据
        billVOPage.setRecords(billPage.getRecords().stream().map(this::getBillVO).collect(Collectors.toList()));
        
        return billVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payBill(BillPayRequest billPayRequest, HttpServletRequest request) {
        if (billPayRequest == null || billPayRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        
        // 检查账单是否存在
        Bill bill = this.getById(billPayRequest.getId());
        if (bill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 检查账单状态
        if (Objects.equals(bill.getStatus(), BillStatusEnum.PAID.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账单已支付");
        }
        
        // 检查用户是否有权限支付该账单
        Property property = propertyMapper.selectById(bill.getPropertyId());
        if (property == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "房产信息不存在");
        }
        
        // 权限检查：管理员可以支付任何账单，业主只能支付自己的账单
        if (!userService.isAdmin(loginUser)) {
            if (!loginUser.getIsOwner()) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非业主无法支付账单");
            }
            
            // 检查该业主是否拥有这个房产（这里需要根据实际业务逻辑调整）
            // 简化处理：假设业主可以支付所有账单
        }
        
        // 更新账单状态
        bill.setStatus(BillStatusEnum.PAID.getValue());
        bill.setPaidTime(new Date());
        
        return this.updateById(bill);
    }

    @Override
    public QueryWrapper<Bill> getQueryWrapper(BillQueryRequest billQueryRequest) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        
        if (billQueryRequest == null) {
            return queryWrapper;
        }
        
        Long id = billQueryRequest.getId();
        Long propertyId = billQueryRequest.getPropertyId();
        String type = billQueryRequest.getType();
        String status = billQueryRequest.getStatus();
        
        // 拼接查询条件
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(propertyId != null && propertyId > 0, "propertyId", propertyId);
        queryWrapper.eq(StringUtils.isNotBlank(type), "type", type);
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        
        return queryWrapper;
    }

    @Override
    public BillVO getBillVO(Bill bill) {
        if (bill == null) {
            return null;
        }
        
        BillVO billVO = new BillVO();
        BeanUtils.copyProperties(bill, billVO);
        
        // 设置类型文本
        for (BillTypeEnum typeEnum : BillTypeEnum.values()) {
            if (typeEnum.getValue().equals(bill.getType())) {
                billVO.setTypeText(typeEnum.getText());
                break;
            }
        }
        
        // 设置状态文本
        for (BillStatusEnum statusEnum : BillStatusEnum.values()) {
            if (statusEnum.getValue().equals(bill.getStatus())) {
                billVO.setStatusText(statusEnum.getText());
                break;
            }
        }
        
        // 设置房产信息
        Property property = propertyMapper.selectById(bill.getPropertyId());
        if (property != null) {
            Building building = buildingService.getById(property.getBuildingId());
            if (building != null) {
                String propertyInfo = building.getBuildingName() + " " + property.getUnitNumber() + "单元 " + property.getRoomNumber() + "号";
                billVO.setPropertyInfo(propertyInfo);
            }
        }
        
        return billVO;
    }
}




