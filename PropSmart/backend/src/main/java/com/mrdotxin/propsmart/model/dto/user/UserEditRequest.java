package com.mrdotxin.propsmart.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 给用户修改个人信息的接口
 */
@Data
public class UserEditRequest implements Serializable {
    /**
     * 新密码
     */
    private String userPassword;
}
