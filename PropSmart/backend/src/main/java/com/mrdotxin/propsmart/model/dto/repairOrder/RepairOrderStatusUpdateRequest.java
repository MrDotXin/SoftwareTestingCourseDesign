package com.mrdotxin.propsmart.model.dto.repairOrder;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;

@Data
@Tag(name = "报修单状态更新请求")
public class RepairOrderStatusUpdateRequest implements Serializable {

    @Schema(description = "报修单ID", required = true)
    private Long id;

    @Schema(description = "状态 pending/completed/cancelled", required = true)
    private String status;

    @Schema(description = "处理说明")
    private String reviewMessage;
}