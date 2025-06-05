package com.mrdotxin.propsmart.model.dto.bill;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModelProperty; // 新增Swagger注解导入
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
    @ApiModelProperty(value = "账单ID") // 新增注解
    private Long id;
    
    /**
     * 房产ID
     */
    @ApiModelProperty(value = "房产ID") // 新增注解
    private Long propertyId;

    /**
     * 费用类型
     */
    @ApiModelProperty(value = "费用类型") // 新增注解
    private String type;
    
    @ApiModelProperty(value = "缴费状态") // 新增注解
    /**
     * 缴费状态
     */
    private String status;
} 