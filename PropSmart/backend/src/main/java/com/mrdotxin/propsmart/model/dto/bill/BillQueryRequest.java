package com.mrdotxin.propsmart.model.dto.bill;

import com.mrdotxin.propsmart.common.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 查询账单请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillQueryRequest extends PageRequest {
    
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
     * 缴费状态
     */
    @Schema(description = "缴费状态")
    private String status;
} 