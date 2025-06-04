package com.mrdotxin.propsmart.model.dto.repairOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "报修单提交请求")
public class RepairOrderSubmitRequest implements Serializable {

    @ApiModelProperty(value = "房产ID", required = true)
    private Long propertyId;

    @ApiModelProperty(value = "问题描述", required = true)
    private String description;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;
}
