package com.mrdotxin.propsmart.model.dto.bill;

import com.mrdotxin.propsmart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询账单请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BillQueryRequest extends PageRequest {
    
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
     * 缴费状态
     */
    private String status;
} 