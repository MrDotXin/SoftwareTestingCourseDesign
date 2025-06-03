package com.mrdotxin.propsmart.controller;


import cn.hutool.core.util.ObjectUtil;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.entity.;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.Service;
import com.mrdotxin.propsmart.service.UserService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/property")
public class PropertiesController {

    @Resource
    private UserService userService;

    @Resource
    private PropertiesService propertiesService;

    @GetMapping("/get/id")
    BaseResponse<Properties> getPropertyById(@RequestParam("id") String id, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjectUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR, "未登录");

        Properties properties = propertiesService.getById(id);

        if (!userService.isAdmin(loginUser) && loginUser.)
    }
}
