package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.model.dto.repairOrder.RepairOrderQueryRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.RepairOrder;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.RepairOrderStatusEnum;
import com.mrdotxin.propsmart.model.vo.RepairOrderVO;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.RepairOrderService;
import com.mrdotxin.propsmart.mapper.RepairOrderMapper;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【repairorder(报修申请)】的数据库操作Service实现
 * @createDate 2025-06-04 09:36:05
 */
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder>
        implements RepairOrderService {

    @Resource
    private UserService userService;

    @Resource
    private PropertyService propertyService;

    @Resource
    private BuildingService buildingService;

    @Override
    public void validateRepairOrder(RepairOrder repairOrder) {
        ThrowUtils.throwIf(ObjectUtil.isNull(repairOrder), ErrorCode.PARAMS_ERROR);

        Long userId = repairOrder.getUserId();
        Long propertyId = repairOrder.getPropertyId();
        String description = repairOrder.getDescription();

        // 基本参数校验
        ThrowUtils.throwIf(ObjectUtil.isNull(userId), ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyId), ErrorCode.PARAMS_ERROR, "房产ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(description), ErrorCode.PARAMS_ERROR, "问题描述不能为空");

        // 描述长度限制
        ThrowUtils.throwIf(description.length() > 1000, ErrorCode.PARAMS_ERROR, "问题描述过长");

        // 验证用户是否存在
        User user = userService.getById(userId);
        ThrowUtils.throwIf(ObjectUtil.isNull(user), ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 验证房产是否存在
        Property property = propertyService.getById(propertyId);
        ThrowUtils.throwIf(ObjectUtil.isNull(property), ErrorCode.NOT_FOUND_ERROR, "房产不存在");
    }

    @Override
    public QueryWrapper<RepairOrder> getQueryWrapper(RepairOrderQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = queryRequest.getId();
        Long userId = queryRequest.getUserId();
        Long propertyId = queryRequest.getPropertyId();
        String description = queryRequest.getDescription();
        String status = queryRequest.getStatus();
        Long reviewerId = queryRequest.getReviewerId();
        Date createStart = queryRequest.getCreateStart();
        Date createEnd = queryRequest.getCreateEnd();
        Date reviewStart = queryRequest.getReviewStart();
        Date reviewEnd = queryRequest.getReviewEnd();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        QueryWrapper<RepairOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjectUtil.isNotNull(userId), "userId", userId);
        queryWrapper.eq(ObjectUtil.isNotNull(propertyId), "propertyId", propertyId);
        queryWrapper.eq(ObjectUtil.isNotNull(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(StrUtil.isNotBlank(status), "status", status);
        queryWrapper.like(StrUtil.isNotBlank(description), "description", description);

        // 创建时间范围查询
        if (ObjectUtil.isNotNull(createStart)) {
            queryWrapper.ge("createTime", createStart);
        }
        if (ObjectUtil.isNotNull(createEnd)) {
            queryWrapper.le("createTime", createEnd);
        }

        // 处理时间范围查询
        if (ObjectUtil.isNotNull(reviewStart)) {
            queryWrapper.ge("reviewTime", reviewStart);
        }
        if (ObjectUtil.isNotNull(reviewEnd)) {
            queryWrapper.le("reviewTime", reviewEnd);
        }

        // 排序处理
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                    sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                    sortField);
        } else {
            queryWrapper.orderByDesc("createTime"); // 默认按创建时间倒序
        }

        return queryWrapper;
    }

    @Override
    public RepairOrderVO getRepairOrderVO(RepairOrder repairOrder) {
        if (repairOrder == null) {
            return null;
        }

        RepairOrderVO vo = new RepairOrderVO();
        BeanUtils.copyProperties(repairOrder, vo);

        // 查询关联的用户信息
        User user = userService.getById(repairOrder.getUserId());
        if (user != null) {
            vo.setUserName(user.getUserName());
            vo.setUserPhone(user.getUserPhoneNumber());
        }

        // 查询关联的房产信息
        Property property = propertyService.getById(repairOrder.getPropertyId());
        if (property != null) {
            vo.setUnitNumber(property.getUnitNumber());
            vo.setRoomNumber(property.getRoomNumber());

            // 查询楼栋信息
            Building building = buildingService.getById(property.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getBuildingName());
            }
        }

        // 查询处理人信息
        if (repairOrder.getReviewerId() != null) {
            User reviewer = userService.getById(repairOrder.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getUserName());
            }
        }

        return vo;
    }

    @Override
    public List<RepairOrderVO> getRepairOrderVOList(List<RepairOrder> repairOrderList) {
        if (CollUtil.isEmpty(repairOrderList)) {
            return new ArrayList<>();
        }
        return repairOrderList.stream().map(this::getRepairOrderVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getStatusStatistics() {
        Map<String, Long> statusStats = new LinkedHashMap<>();

        // 查询所有状态的数量
        List<Map<String, Object>> stats = this.baseMapper.selectMaps(
                new QueryWrapper<RepairOrder>()
                        .select("status, count(*) as count")
                        .groupBy("status")
        );

        // 初始化所有状态为0
        for (RepairOrderStatusEnum status : RepairOrderStatusEnum.values()) {
            statusStats.put(status.getText(), 0L);
        }

        // 填充实际统计数据
        for (Map<String, Object> stat : stats) {
            String status = (String) stat.get("status");
            Long count = (Long) stat.get("count");
            statusStats.put(RepairOrderStatusEnum.getTextByValue(status), count);
        }

        return statusStats;
    }
}




