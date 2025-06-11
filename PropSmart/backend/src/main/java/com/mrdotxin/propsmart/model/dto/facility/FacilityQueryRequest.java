package com.mrdotxin.propsmart.model.dto.facility;

import com.mrdotxin.propsmart.common.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityQueryRequest extends PageRequest implements Serializable {
    /**
     *
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 设施名称
     */
    @Schema(description = "设施名称")
    private String facilityName;

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
