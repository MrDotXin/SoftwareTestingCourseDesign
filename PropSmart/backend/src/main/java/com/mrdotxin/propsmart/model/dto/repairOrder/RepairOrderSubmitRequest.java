package com.mrdotxin.propsmart.model.dto.repairOrder;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;

@Data
@Tag(name = "报修单提交请求")
public class RepairOrderSubmitRequest implements Serializable {

    @Schema(description = "房产ID", required = true)
    private Long propertyId;

    @Schema(description = "问题描述", required = true)
    private String description;

    @Schema(description = "联系电话")
    private String contactPhone;
}
