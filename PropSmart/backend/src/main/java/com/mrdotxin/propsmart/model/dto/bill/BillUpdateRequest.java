package com.mrdotxin.propsmart.model.dto.bill;


import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "账单ID")
    private Long id;
    
    /**
     * 房产ID
     */
    @Schema(description = "房产ID")
    private Long propertyId;

    /**
     * 费用类型
     */
    @Schema(description = "费用类型")
    private String type;

    /**
     * 金额
     */
    @Schema(description = "金额")
    private BigDecimal amount;

    /**
     * 截止日期
     */
    @Schema(description = "截止日期")
    private Date deadline;
    
    /**
     * 缴费状态
     */
    @Schema(description = "缴费状态")
    private String status;
} 