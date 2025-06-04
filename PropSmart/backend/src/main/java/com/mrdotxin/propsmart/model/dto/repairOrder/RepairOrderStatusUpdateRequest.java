package com.mrdotxin.propsmart.model.dto.repairOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "报修单状态更新请求")
public class RepairOrderStatusUpdateRequest implements Serializable {

    @ApiModelProperty(value = "报修单ID", required = true)
    private Long id;

    @ApiModelProperty(value = "状态 pending/completed/cancelled", required = true)
    private String status;

    @ApiModelProperty(value = "处理说明")
    private String reviewMessage;
}