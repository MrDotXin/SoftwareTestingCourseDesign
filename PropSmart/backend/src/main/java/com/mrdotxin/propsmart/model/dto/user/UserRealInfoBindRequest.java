package com.mrdotxin.propsmart.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRealInfoBindRequest implements Serializable {

    private Long id;
    /**
     * 姓名
     */
    private String userRealName;

    /**
     * 身份证
     */
    private String userIdCardNumber;

    /**
     * 用户手机号
     */
    private String userPhoneNumber;

    private static final long serialVersionUID = 1L;
}
