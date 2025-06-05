package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.UserMapper;
import com.mrdotxin.propsmart.model.dto.user.UserQueryRequest;
import com.mrdotxin.propsmart.model.dto.user.UserRealInfoBindRequest;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.UserRoleEnum;
import com.mrdotxin.propsmart.model.vo.LoginUserVO;
import com.mrdotxin.propsmart.model.vo.UserVO;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.utils.FormatUtils;
import com.mrdotxin.propsmart.utils.SqlUtils;
import com.mrdotxin.propsmart.websocket.WebSocketConnection;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mrdotxin.propsmart.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "mrdotxin";
    private final WebSocketConnection webSocketService;

    public UserServiceImpl(WebSocketConnection webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }

            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserName(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        ThrowUtils.throwIf(WebSocketConnection.existsUser(user.getId()), ErrorCode.OPERATION_ERROR, "用户已经在其它地方登录了!");
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        User user = (User) getLoginUser(request);

        WebSocketConnection.closeConnection(user.getId());

        request.getSession().removeAttribute(USER_LOGIN_STATE);

        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public User getByIdCardNumber(String idCardNumber) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userIdCardNumber", idCardNumber);

        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void updateUserOwnerStatus(String idCardNumber, Boolean isOwner) {
        if (ObjectUtil.isNull(idCardNumber)) {
            return;
        }
        // 删除房产，清除对应的用户认证信息
        User user = this.getByIdCardNumber(idCardNumber);
        if (ObjectUtil.isNotNull(user)) {
            user.setIsOwner(false);
            this.updateById(user);
        }
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String userPhoneNumber = userQueryRequest.getUserPhoneNumber();
        String userRealName = userQueryRequest.getUserRealName();
        String userIdCardNumber = userQueryRequest.getUserIdCardNumber();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userPhoneNumber), "userPhoneNumber", userPhoneNumber);
        queryWrapper.like(StringUtils.isNotBlank(userRealName), "userRealName", userRealName);
        queryWrapper.like(StringUtils.isNotBlank(userIdCardNumber), "userIdCardNumber", userIdCardNumber);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public User bindUserRealInfo(UserRealInfoBindRequest userRealInfoBindRequest, User user) {
        ThrowUtils.throwIf(ObjectUtil.isNotNull(user.getUserIdCardNumber()), ErrorCode.PARAMS_ERROR, "当前用户已存在绑定信息");

        String identity = userRealInfoBindRequest.getUserIdCardNumber();
        String realName = userRealInfoBindRequest.getUserRealName();
        if (ObjectUtil.isAllNotEmpty(identity, realName)) {
            ThrowUtils.throwIf(FormatUtils.isValidNameAndIdCard(realName, identity), ErrorCode.PARAMS_ERROR, "用户身份证或者姓名格式不合法!");
            ThrowUtils.throwIf(existsWithField("userIdCardNumber", identity), ErrorCode.PARAMS_ERROR, "这个身份证已经被绑定了!");

            user.setUserRealName(realName);
            user.setUserIdCardNumber(identity);
        }

        String userPhoneNumber = userRealInfoBindRequest.getUserPhoneNumber();
        if (ObjectUtil.isNotNull(userPhoneNumber)) {
            ThrowUtils.throwIf(FormatUtils.isValidPhone(userPhoneNumber), ErrorCode.PARAMS_ERROR, "电话格式不合法!");
            ThrowUtils.throwIf(existsWithField("userPhoneNumber", userPhoneNumber), ErrorCode.PARAMS_ERROR, "这个电话号码已经被绑定了!");
            user.setUserPhoneNumber(userPhoneNumber);
        }

        return user;
    }

    public List<Long> listUserIdByBuildingId(Long buildingId) {
        return this.baseMapper.selectUserByBuildingId(buildingId);
    }

    @Override
    public List<Long> listAdminId() {
        return this.baseMapper.selectAllAdminId();
    }

    @Override
    public Boolean existsWithField(String fieldName, Object value) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public User getByFiled(String fieldName, Object value) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, value);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
