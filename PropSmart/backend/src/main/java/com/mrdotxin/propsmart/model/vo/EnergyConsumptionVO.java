package com.mrdotxin.propsmart.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class EnergyConsumptionVO {

    private Long id;

    private Long propertyId;

    private String buildingName;

    private String unitNumber;

    private String roomNumber;

    private String energyType;

    private String energyTypeText;

    private Double consumption;

    private Double price;

    private Double totalCost;

    private Date measureTime;

    private Date createTime;

    public String getEnergyTypeText() {
        switch (this.energyType) {
            case "electricity": return "电力";
            case "water": return "水";
            default: return "未知";
        }
    }
}