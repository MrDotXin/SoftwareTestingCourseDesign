package com.mrdotxin.propsmart.model.dto.facility;

import lombok.Data;

import java.io.Serializable;

@Data
public class FacilityAddRequest implements Serializable {
    /**
     * 设施名称
     */
    private String facilityName;

    /**
     * 位置
     */
    private String location;

    /**
     * 容量
     */
    private Integer capacity;

    /**
     * 描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
