package com.mrdotxin.propsmart.model.dto.facility;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class FacilityAddRequest implements Serializable {
    /**
     * 设施名称
     */
    @Schema(description = "设施名称")
    private String facilityName;

    /**
     * 位置
     */
    @Schema(description = "位置")
    private String location;

    /**
     * 容量
     */
    @Schema(description = "容量")
    private Integer capacity;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    private static final long serialVersionUID = 1L;
}
