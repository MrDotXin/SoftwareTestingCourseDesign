package com.mrdotxin.propsmart.model.vo;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 */
@Data
@Tag(name = "用户视图对象")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户昵称
     */

    private String userRealName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

        /**
     * 联系电话
     */
    private String userPhoneNumber;

    /**
     * 身份证
     */
    private String userIdCardNumber;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 是不是业主
     */
    private Boolean isOwner;

    /**
     * 创建时间
     */
    private Date createTime;
}