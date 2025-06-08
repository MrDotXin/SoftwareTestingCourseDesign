package com.mrdotxin.propsmart.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeoCoordinationEnum {

    EPSG3857("EPSG:3857"),
    EPSG4326("EPSG:4326");


    final String value;

}
