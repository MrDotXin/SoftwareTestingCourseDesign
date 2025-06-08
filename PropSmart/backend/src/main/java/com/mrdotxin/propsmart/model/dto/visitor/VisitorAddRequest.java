package com.mrdotxin.propsmart.model.dto.visitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建访客申请请求
 */
@Data
public class VisitorAddRequest implements Serializable {

    /**
     * 访客姓名
     */
    @ApiModelProperty(value = "访客姓名", required = true)
    private String visitorName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", required = true)
    private String idNumber;

    /**
     * 访问原因
     */
    @ApiModelProperty(value = "访问原因", required = true)
    private String visitReason;

    /**
     * 预计访问时间
     */
    @ApiModelProperty(value = "预计访问时间", required = true)
    private Date visitTime;

    /**
     * 预计时长（小时）
     */
    @ApiModelProperty(value = "预计时长（小时）", required = true)
    private Integer duration;

    private static final long serialVersionUID = 1L;
} 