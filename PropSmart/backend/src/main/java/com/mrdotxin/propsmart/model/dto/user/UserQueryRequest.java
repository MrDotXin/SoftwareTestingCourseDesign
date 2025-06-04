package com.mrdotxin.propsmart.model.dto.user;

import com.mrdotxin.propsmart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户手机号
     */
    private String userPhoneNumber;

    /**
     * 用户姓名
     */
    private String userRealName;

    /**
     * 用户身份证号
     */
    private String userIdCardNumber;

    private static final long serialVersionUID = 1L;
}