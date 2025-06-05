package com.mrdotxin.propsmart.model.dto.building;

import com.mrdotxin.propsmart.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuildingUpdateRequest implements Serializable {
    /**
     *
     */
    private Long id;

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
