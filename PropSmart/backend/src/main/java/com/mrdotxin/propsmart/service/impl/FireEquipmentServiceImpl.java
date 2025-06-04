package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.mapper.FireEquipmentMapper;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentAddRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentQueryRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentUpdateRequest;
import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.UserRoleEnum;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.FireEquipmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 消防设备服务实现
 */
@Service
public class FireEquipmentServiceImpl extends ServiceImpl<FireEquipmentMapper, FireEquipment> 
        implements FireEquipmentService {

    @Resource
    private BuildingService buildingService;

    @Override
    public Long addFireEquipment(FireEquipmentAddRequest fireEquipmentAddRequest, User loginUser) {
        if (fireEquipmentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验用户权限
        if (!UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可以添加消防设备");
        }
        
        // 校验所属楼栋是否存在
        Long buildingId = fireEquipmentAddRequest.getBuildingId();
        if (buildingId == null || buildingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "楼栋不能为空");
        }
        if (buildingService.getById(buildingId) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "所选楼栋不存在");
        }
        
        FireEquipment fireEquipment = new FireEquipment();
        BeanUtils.copyProperties(fireEquipmentAddRequest, fireEquipment);
        
        // 设置默认值
        if (fireEquipment.getCurrentStatus() == null) {
            fireEquipment.setCurrentStatus("normal");
        }
        
        // 保存到数据库
        boolean saveResult = this.save(fireEquipment);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        
        return fireEquipment.getId();
    }

    @Override
    public boolean updateFireEquipment(FireEquipmentUpdateRequest fireEquipmentUpdateRequest, User loginUser) {
        if (fireEquipmentUpdateRequest == null || fireEquipmentUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验用户权限
        if (!UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可以更新消防设备");
        }
        
        // 校验设备是否存在
        Long id = fireEquipmentUpdateRequest.getId();
        FireEquipment oldFireEquipment = this.getById(id);
        if (oldFireEquipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "设备不存在");
        }
        
        // 校验所属楼栋是否存在
        Long buildingId = fireEquipmentUpdateRequest.getBuildingId();
        if (buildingId != null && buildingId > 0) {
            if (buildingService.getById(buildingId) == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "所选楼栋不存在");
            }
        }
        
        // 如果有巡检信息更新，记录当前登录用户为最后巡检人
        if (StringUtils.isNotBlank(fireEquipmentUpdateRequest.getInspectionRemarks()) 
                || "faulty".equals(fireEquipmentUpdateRequest.getCurrentStatus())
                || "needs_inspection".equals(fireEquipmentUpdateRequest.getCurrentStatus())) {
            fireEquipmentUpdateRequest.setLastInspectorId(loginUser.getId());
            fireEquipmentUpdateRequest.setLastInspectionTime(new Date());
        }
        
        FireEquipment fireEquipment = new FireEquipment();
        BeanUtils.copyProperties(fireEquipmentUpdateRequest, fireEquipment);
        
        return this.updateById(fireEquipment);
    }

    @Override
    public QueryWrapper<FireEquipment> getQueryWrapper(FireEquipmentQueryRequest fireEquipmentQueryRequest) {
        if (fireEquipmentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        
        Long id = fireEquipmentQueryRequest.getId();
        Long buildingId = fireEquipmentQueryRequest.getBuildingId();
        String currentStatus = fireEquipmentQueryRequest.getCurrentStatus();
        Long lastInspectorId = fireEquipmentQueryRequest.getLastInspectorId();
        Date lastInspectionTimeStart = fireEquipmentQueryRequest.getLastInspectionTimeStart();
        Date lastInspectionTimeEnd = fireEquipmentQueryRequest.getLastInspectionTimeEnd();
        Date nextInspectionDueStart = fireEquipmentQueryRequest.getNextInspectionDueStart();
        Date nextInspectionDueEnd = fireEquipmentQueryRequest.getNextInspectionDueEnd();
        
        QueryWrapper<FireEquipment> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(buildingId != null && buildingId > 0, "buildingId", buildingId);
        queryWrapper.eq(StringUtils.isNotBlank(currentStatus), "currentStatus", currentStatus);
        queryWrapper.eq(lastInspectorId != null && lastInspectorId > 0, "lastInspectorId", lastInspectorId);
        
        queryWrapper.ge(lastInspectionTimeStart != null, "lastInspectionTime", lastInspectionTimeStart);
        queryWrapper.le(lastInspectionTimeEnd != null, "lastInspectionTime", lastInspectionTimeEnd);
        queryWrapper.ge(nextInspectionDueStart != null, "nextInspectionDue", nextInspectionDueStart);
        queryWrapper.le(nextInspectionDueEnd != null, "nextInspectionDue", nextInspectionDueEnd);
        
        return queryWrapper;
    }

    @Override
    public List<FireEquipment> getPendingInspectionEquipment() {
        QueryWrapper<FireEquipment> queryWrapper = new QueryWrapper<>();
        
        // 状态为needs_inspection或者已过期需要巡检的设备
        queryWrapper.eq("currentStatus", "needs_inspection")
                .or()
                .lt("nextInspectionDue", new Date());
                
        return this.list(queryWrapper);
    }
}




