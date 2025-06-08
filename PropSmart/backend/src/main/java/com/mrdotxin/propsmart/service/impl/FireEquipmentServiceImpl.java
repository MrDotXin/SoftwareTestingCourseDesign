package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.service.FireEquipmentService;
import com.mrdotxin.propsmart.mapper.mysql.FireequipmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* 消防设备管理服务实现
*/
@Slf4j
@Service
public class FireEquipmentServiceImpl extends ServiceImpl<FireequipmentMapper, FireEquipment>
    implements FireEquipmentService {

    @Override
    public List<FireEquipment> getEquipmentNeedingInspection(int daysThreshold) {
        Date now = new Date();
        Date thresholdDate = DateUtil.offsetDay(now, daysThreshold);
        
        LambdaQueryWrapper<FireEquipment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(FireEquipment::getNextInspectionDue, thresholdDate);
        
        return this.list(queryWrapper);
    }

    @Override
    public List<FireEquipment> getEquipmentByBuilding(Long buildingId) {
        LambdaQueryWrapper<FireEquipment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FireEquipment::getBuildingId, buildingId)
                   .orderByAsc(FireEquipment::getNextInspectionDue);
        
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean performInspection(Long equipmentId, Long inspectorId, String remarks) {
        FireEquipment equipment = this.getById(equipmentId);
        if (equipment == null) {
            log.error("设备ID不存在: {}", equipmentId);
            return false;
        }
        
        // 更新巡检信息
        equipment.setLastInspectorId(inspectorId);
        equipment.setLastInspectionTime(new Date());
        equipment.setCurrentStatus("normal");
        
        if (remarks != null && !remarks.isEmpty()) {
            equipment.setInspectionRemarks(remarks);
        }
        
        // 设置下次巡检时间，默认30天后
        Date nextMonth = DateUtil.offsetDay(new Date(), 30);
        equipment.setNextInspectionDue(nextMonth);
        
        return this.updateById(equipment);
    }

    @Override
    public boolean updateEquipmentStatus(Long equipmentId, String status) {
        LambdaUpdateWrapper<FireEquipment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FireEquipment::getId, equipmentId)
                    .set(FireEquipment::getCurrentStatus, status)
                    .set(FireEquipment::getUpdateTime, new Date());
        
        return this.update(updateWrapper);
    }

    @Override
    public Page<FireEquipment> pageByStatus(String status, long page, long size) {
        LambdaQueryWrapper<FireEquipment> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(FireEquipment::getCurrentStatus, status);
        }
        
        queryWrapper.orderByAsc(FireEquipment::getNextInspectionDue);
        
        return this.page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public boolean setNextInspectionDate(Long equipmentId, Date nextDate) {
        LambdaUpdateWrapper<FireEquipment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FireEquipment::getId, equipmentId)
                    .set(FireEquipment::getNextInspectionDue, nextDate)
                    .set(FireEquipment::getUpdateTime, new Date());
        
        return this.update(updateWrapper);
    }
    
    @Override
    public long countByStatus(String status) {
        LambdaQueryWrapper<FireEquipment> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(FireEquipment::getCurrentStatus, status);
        }
        
        return this.count(queryWrapper);
    }
}




