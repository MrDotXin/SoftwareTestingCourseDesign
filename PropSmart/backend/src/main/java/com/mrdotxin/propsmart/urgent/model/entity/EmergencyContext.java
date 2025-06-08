package com.mrdotxin.propsmart.urgent.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class EmergencyContext {
    private String emergencyType;        // 灾害类型

    private List<EmergencyLocation> disasterLocation;   // 灾害发生地点

    private int affectedPeopleCount;     // 受影响人数

    private List<EvacuationPoint> evacuationPoints;    // 疏散点
}