package com.mrdotxin.propsmart.model.dto.visitor;

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
    private String visitorName;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 访问原因
     */
    private String visitReason;

    /**
     * 预计访问时间
     */
    private Date visitTime;

    /**
     * 预计时长（小时）
     */
    private Integer duration;

    private static final long serialVersionUID = 1L;
} 