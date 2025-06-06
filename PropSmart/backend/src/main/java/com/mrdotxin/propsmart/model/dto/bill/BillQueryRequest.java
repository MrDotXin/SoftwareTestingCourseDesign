package com.mrdotxin.propsmart.model.dto.bill;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 查询账单请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BillQueryRequest extends PageRequest {
    
    /**
     * 账单ID
     */
    @ApiModelProperty(value = "账单ID")
    private Long id;
    
    /**
     * 房产ID
     */
    @ApiModelProperty(value = "房产ID")
    private Long propertyId;

    /**
     * 费用类型
     */
    @ApiModelProperty(value = "费用类型")
    private String type;
    
    /**
     * 缴费状态
     */
    @ApiModelProperty(value = "缴费状态")
    private String status;
} 