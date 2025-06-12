package com.mrdotxin.propsmart.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
