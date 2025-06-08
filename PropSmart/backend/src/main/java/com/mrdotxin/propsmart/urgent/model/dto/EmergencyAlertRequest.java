package com.mrdotxin.propsmart.urgent.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmergencyAlertRequest implements Serializable {
    String emergencyType;
}
