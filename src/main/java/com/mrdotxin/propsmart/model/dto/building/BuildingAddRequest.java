package com.mrdotxin.propsmart.model.dto.building;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuildingAddRequest implements Serializable {
    /**
     * 楼栋名称/编号
     */
    private String buildingName;

    /**
     * 地理位置
     */
    private String address;

    private static final long serialVersionUID = 1L;
}
