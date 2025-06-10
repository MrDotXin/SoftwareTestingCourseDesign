package com.mrdotxin.propsmart.model.vo;

import com.mrdotxin.propsmart.model.enums.RepairOrderStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class RepairOrderVO {

    private Long id;

    private Long userId;

    private String userName;

    private String userPhone;

    private Long propertyId;

    private String buildingName;

    private String unitNumber;

    private String roomNumber;

    private String description;

    private String status;

    private String statusText;

    private Date createTime;

    private Date updateTime;

    private Long reviewerId;

    private String reviewerName;

    private Date reviewTime;

    private String reviewMessage;

    public String getStatusText() {
        return RepairOrderStatusEnum.getTextByValue(this.status);
    }
}
