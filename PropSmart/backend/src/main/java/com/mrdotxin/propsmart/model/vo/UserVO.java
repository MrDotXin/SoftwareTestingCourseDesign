package com.mrdotxin.propsmart.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 */
@Data
@ApiModel("用户视图对象")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "用户ID", example = "123456")
    private Long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", example = "张三")
    private String userName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像URL", example = "https://example.com/avatar.jpg")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty(value = "用户简介", example = "这是一个用户简介示例")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色", example = "user", allowableValues = "user,admin,ban")
    private String userRole;

    /**
     * 是不是业主
     */
    @ApiModelProperty(value = "是否是业主", example = "true")
    private Boolean isOwner;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T00:00:00")
    private Date createTime;
}