package com.mrdotxin.propsmart.model.dto.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 更新账单请求
 */
@Data
public class BillUpdateRequest {
    
    /**
     * 账单ID
     */
    private Long id;
    
    /**
     * 房产ID
     */
    private Long propertyId;

    /**
     * 费用类型
     */
    private String type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 截止日期
     */
    private Date deadline;
    
    /**
     * 缴费状态
     */
    private String status;
} 