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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "房产资源管理")
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
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取房产信息")
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
    @ApiOperation(value = "获取房产信息", notes = "这个函数可以通过传进来的身份证来查询名下房产, 但只是本人或者管理员")
    BaseResponse<List<Property>> getPropertyByUserIdentity(@RequestParam("idCardNumber") String idCardNumber, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjectUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR, "未登录");
        if (!userService.isAdmin(loginUser) && !idCardNumber.equals(loginUser.getUserIdCardNumber())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足");
        }

        List<Property> property = propertyService.listByFiled("ownerIdentity", idCardNumber);
        ThrowUtils.throwIf(CollUtil.isEmpty(property), ErrorCode.NOT_FOUND_ERROR, "房产不存在");

        return ResultUtils.success(property);
    }


    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    @ApiOperation(value = "添加房产信息", notes = "如果传入的房产本身是带身份证的, 那么如果用户存在, 就会自动关联身份")
    BaseResponse<Boolean> addProperty(@RequestBody PropertyAddRequest propertyAddRequest, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyAddRequest), ErrorCode.PARAMS_ERROR);
        Property property = new Property();
        property.setOwnerIdentity(propertyAddRequest.getOwnerIdentity());
        property.setUnitNumber(propertyAddRequest.getUnitNumber());
        property.setRoomNumber(propertyAddRequest.getRoomNumber());
        property.setArea(propertyAddRequest.getArea());

        Building building = buildingService.getByBuildingName(propertyAddRequest.getBuildingName());
        property.setBuildingId(building.getId());

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
    @ApiOperation(value = "更新房产信息", notes = "更新的时候会自动修改关联的用户身份")
    BaseResponse<Boolean> updateProperty(@RequestBody PropertyUpdateRequest propertyUpdateRequest, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyUpdateRequest), ErrorCode.PARAMS_ERROR);

        Property property = new Property();
        BeanUtils.copyProperties(propertyUpdateRequest, property);

        propertyService.validateProperty(property);

        Property oldProperty = propertyService.getById(property.getId());
        if (ObjectUtil.isNotNull(oldProperty.getOwnerIdentity()) || ObjectUtil.isNotNull(property.getOwnerIdentity())) {
            if (!(ObjectUtil.isAllNotEmpty(oldProperty.getOwnerIdentity(), property.getOwnerIdentity()) &&
                    !oldProperty.getOwnerIdentity().equals(property.getOwnerIdentity()))) {

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
    @ApiOperation(value = "删除房产信息", notes = "删除房产信息, 同时更新对应用户的身份")
    BaseResponse<Boolean> deleteProperty(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
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
    @ApiOperation(value = "分页获得房产信息")
    BaseResponse<Page<Property>> listPropertyByPage(@RequestBody PropertyQueryRequest propertyQueryRequest, HttpServletRequest httpServletRequest) {
        long current = propertyQueryRequest.getCurrent();
        long size = propertyQueryRequest.getPageSize();
        Page<Property> propertyPage = propertyService.page(new Page<>(current, size),
                propertyService.getQueryWrapper(propertyQueryRequest));
        return ResultUtils.success(propertyPage);
    }
}
