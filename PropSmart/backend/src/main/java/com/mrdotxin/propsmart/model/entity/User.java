package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User implements Serializable {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
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
     * 用户真实姓名
     */
    private String userRealName;

    /**
     * 用户角色：ROLE_USER/ROLE_ADMIN/ROLE_BAN
     */
    private String userRole;

    /**
     * 是否是业主
     */
    private Boolean isOwner;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}