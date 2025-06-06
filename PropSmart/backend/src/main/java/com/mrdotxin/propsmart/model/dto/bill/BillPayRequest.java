package com.mrdotxin.propsmart.model.dto.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 缴费请求
 */
@Data
public class BillPayRequest {
    
    /**
     * 账单ID
     */
    @ApiModelProperty(value = "账单ID")
    private Long id;
} 