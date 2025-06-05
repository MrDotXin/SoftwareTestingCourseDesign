package com.mrdotxin.propsmart.model.dto.facility.reservation;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新设施预订请求（管理员审批）
 */
@Data
public class FacilityReservationUpdateRequest implements Serializable {
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 状态（success/rejected）
     */
    private String status;
    
    /**
     * 审批原因
     */
    private String reviewMessage;
    
    private static final long serialVersionUID = 1L;
} 