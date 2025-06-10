package com.mrdotxin.propsmart.model.dto.bill;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 缴费请求
 */
@Data
public class BillPayRequest {
    
    /**
     * 账单ID
     */
    @Schema(description = "账单ID")
    private Long id;
} 