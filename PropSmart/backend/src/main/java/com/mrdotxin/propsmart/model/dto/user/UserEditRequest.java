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

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
