package com.mrdotxin.propsmart.model.dto.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 添加账单请求
 */
@Data
public class BillAddRequest {
    
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
} 