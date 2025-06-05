package com.mrdotxin.propsmart.model.dto.bill;

import io.swagger.annotations.ApiModelProperty; // 新增Swagger注解导入
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

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额") // 新增注解
    private BigDecimal amount;

    /**
     * 截止日期
     */
    @ApiModelProperty(value = "截止日期") // 新增注解

    private Date deadline;
    
    /**
     * 缴费状态
     */
    @ApiModelProperty(value = "缴费状态") // 新增注解
    private String status;
} 