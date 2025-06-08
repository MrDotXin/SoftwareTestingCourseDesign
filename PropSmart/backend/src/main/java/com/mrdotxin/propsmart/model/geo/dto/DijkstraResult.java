package com.mrdotxin.propsmart.model.geo.dto;

import lombok.Data;

@Data
public class DijkstraResult {
    private Integer seq;
    private Long node;
    private Long edge;
    private Double cost;
    private Double aggCost;
}
