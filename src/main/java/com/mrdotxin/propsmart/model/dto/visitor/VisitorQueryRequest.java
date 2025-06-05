package com.mrdotxin.propsmart.model.dto.visitor;

import com.mrdotxin.propsmart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询访客申请请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VisitorQueryRequest extends PageRequest implements Serializable {

    /**
     * 访客姓名
     */
    private String visitorName;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 被访用户id
     */
    private Long userId;

    /**
     * 审批状态（pending/approved/rejected）
     */
    private String reviewStatus;

    /**
     * 访问时间
     */
    private Date visitTime;

    private static final long serialVersionUID = 1L;
} 