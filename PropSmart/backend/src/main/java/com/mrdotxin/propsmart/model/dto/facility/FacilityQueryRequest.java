package com.mrdotxin.propsmart.model.dto.facility;

import com.mrdotxin.propsmart.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityQueryRequest extends PageRequest implements Serializable {
    /**
     *
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 设施名称
     */
    @ApiModelProperty(value = "设施名称")
    private String facilityName;

    /**
     * 位置
     */
    @ApiModelProperty(value = "位置")
    private String location;

    /**
     * 容量
     */
    @ApiModelProperty(value = "容量")
    private Integer capacity;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    private static final long serialVersionUID = 1L;
}
