package com.mrdotxin.propsmart.model.vo;

import com.mrdotxin.propsmart.model.enums.RepairOrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "报修单视图对象")
public class RepairOrderVO {

    @ApiModelProperty(value = "报修单ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户电话")
    private String userPhone;

    @ApiModelProperty(value = "房产ID")
    private Long propertyId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildingName;

    @ApiModelProperty(value = "单元号")
    private String unitNumber;

    @ApiModelProperty(value = "房间号")
    private String roomNumber;

    @ApiModelProperty(value = "问题描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态文本")
    private String statusText;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "处理人ID")
    private Long reviewerId;

    @ApiModelProperty(value = "处理人姓名")
    private String reviewerName;

    @ApiModelProperty(value = "处理时间")
    private Date reviewTime;

    @ApiModelProperty(value = "处理说明")
    private String reviewMessage;

    public String getStatusText() {
        return RepairOrderStatusEnum.getTextByValue(this.status);
    }
}
