package com.mrdotxin.propsmart.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.*;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.property.PropertyAddRequest;
import com.mrdotxin.propsmart.model.dto.property.PropertyQueryRequest;
import com.mrdotxin.propsmart.model.dto.property.PropertyUpdateRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "房产资源管理")
@RestController
@RequestMapping("/property")
public class PropertyController {

    @Resource
    private UserService userService;

    @Resource
    private PropertyService propertyService;

    @Resource
    private BuildingService buildingService;

    @GetMapping("/get/id")
    @Operation(method = "获取房产信息")
    BaseResponse<Property> getPropertyById(@RequestParam("id") String id, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjectUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR, "未登录");

        Property property = propertyService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isNull(property), ErrorCode.NOT_FOUND_ERROR, "房产不存在");
        if (!userService.isAdmin(loginUser) && !property.getOwnerIdentity().equals(loginUser.getUserIdCardNumber())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足");
        }

        return ResultUtils.success(property);
    }

    @GetMapping("/get/idCardNumber")
    @Operation(method = "获取房产信息")
    BaseResponse<List<Property>> getPropertyByUserIdentity(@RequestParam("idCardNumber") String idCardNumber, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjectUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR, "未登录");
        if (!userService.isAdmin(loginUser) && !idCardNumber.equals(loginUser.getUserIdCardNumber())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足");
        }

        List<Property> property = propertyService.listByField("ownerIdentity", idCardNumber);
        ThrowUtils.throwIf(CollUtil.isEmpty(property), ErrorCode.NOT_FOUND_ERROR, "房产不存在");

        return ResultUtils.success(property);
    }


    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    @Operation(method = "添加房产信息")
    BaseResponse<Boolean> addProperty(@RequestBody PropertyAddRequest propertyAddRequest) {
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyAddRequest), ErrorCode.PARAMS_ERROR);
        Property property = new Property();
        property.setOwnerIdentity(propertyAddRequest.getOwnerIdentity());
        property.setUnitNumber(propertyAddRequest.getUnitNumber());
        property.setRoomNumber(propertyAddRequest.getRoomNumber());
        property.setArea(propertyAddRequest.getArea());

        ThrowUtils.throwIf(buildingService.existsWithField("id", propertyAddRequest.getBuildingId()), ErrorCode.NOT_FOUND_ERROR);
        property.setBuildingId(propertyAddRequest.getBuildingId());

        if (ObjectUtil.isNotNull(property.getOwnerIdentity())) {
            userService.updateUserOwnerStatus(property.getOwnerIdentity(), true);
        }
        propertyService.validateProperty(property);

        boolean result = propertyService.save(property);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "无法保存");

        if (ObjectUtil.isNotNull(property.getOwnerIdentity())) {
            userService.updateUserOwnerStatus(property.getOwnerIdentity(), true);
        }

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    @Operation(method = "更新房产信息")
    BaseResponse<Boolean> updateProperty(@RequestBody PropertyUpdateRequest propertyUpdateRequest) {
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyUpdateRequest), ErrorCode.PARAMS_ERROR);

        Property property = new Property();
        BeanUtils.copyProperties(propertyUpdateRequest, property);

        propertyService.validateProperty(property);

        Property oldProperty = propertyService.getById(property.getId());
        if (ObjectUtil.isNotNull(oldProperty.getOwnerIdentity()) || ObjectUtil.isNotNull(property.getOwnerIdentity())) {
            if (!(ObjectUtil.isAllNotEmpty(oldProperty.getOwnerIdentity(), property.getOwnerIdentity()) &&
                    oldProperty.getOwnerIdentity().equals(property.getOwnerIdentity()))) {

                if (ObjectUtil.isNotNull(oldProperty.getOwnerIdentity())) {
                    userService.updateUserOwnerStatus(oldProperty.getOwnerIdentity(), false);
                }

                if (ObjectUtil.isNotNull(property.getOwnerIdentity())) {
                    userService.updateUserOwnerStatus(property.getOwnerIdentity(), true);
                }
            }
        }

        boolean result = propertyService.updateById(property);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "无法更新");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    @Operation(method = "删除房产信息")
    BaseResponse<Boolean> deleteProperty(@RequestBody DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();

        Property oldProperty = propertyService.getById(id);
        if (ObjectUtil.isNotNull(oldProperty.getOwnerIdentity())) {
            userService.updateUserOwnerStatus(oldProperty.getOwnerIdentity(), false);
        }

        boolean result = propertyService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "无法删除");

        return ResultUtils.success(true);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    @Operation(method = "分页获得房产信息")
    BaseResponse<Page<Property>> listPropertyByPage(@RequestBody PropertyQueryRequest propertyQueryRequest) {
        long current = propertyQueryRequest.getCurrent();
        long size = propertyQueryRequest.getPageSize();
        Page<Property> propertyPage = propertyService.page(new Page<>(current, size),
                propertyService.getQueryWrapper(propertyQueryRequest));
        return ResultUtils.success(propertyPage);
    }
}
