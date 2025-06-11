package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.user.UserQueryRequest;
import com.mrdotxin.propsmart.model.dto.user.UserRealInfoBindRequest;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.vo.LoginUserVO;
import com.mrdotxin.propsmart.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);



    /**
     * 是否为管理员
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     */
    List<UserVO> getUserVO(List<User> userList);

    List<Long> listUserIdByBuildingId(Long buildingId);

    List<Long> listAdminId();

    List<Long> listUserIdAll();

    User getByIdCardNumber(String idCardNumber);

    void updateUserOwnerStatus(String idCardNumber, Boolean isOwner);

    /**
     * 获取查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    /**
     * 绑定身份证信息
     */
    User bindUserRealInfo(UserRealInfoBindRequest userRealInfoBindRequest, User user);

    Boolean existsWithField(String fieldName, Object value);

    User getByField(String fieldName, Object value);
}
