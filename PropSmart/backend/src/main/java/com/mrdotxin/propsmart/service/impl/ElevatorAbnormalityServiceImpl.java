package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.ElevatorAbnormalityMapper;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.AbnormalityLevelEnum;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import com.mrdotxin.propsmart.service.ElevatorNotificationService;
import com.mrdotxin.propsmart.service.ElevatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电梯异常服务实现类
 */
@Slf4j
@Service
public class ElevatorAbnormalityServiceImpl extends ServiceImpl<ElevatorAbnormalityMapper, ElevatorAbnormality> implements ElevatorAbnormalityService {
    
    @Resource
    private ElevatorService elevatorService;
    
    @Resource
    private ElevatorNotificationService elevatorNotificationService;
    
    @Override
    public boolean save(ElevatorAbnormality abnormality) {
        // 设置创建时间
        if (abnormality.getCreateTime() == null) {
            abnormality.setCreateTime(new Date());
        }
        
        // 保存异常记录
        boolean result = super.save(abnormality);
        
        // 发送通知
        if (result) {
            // 获取电梯信息
            Elevator elevator = elevatorService.getById(abnormality.getElevatorId());
            if (elevator != null) {
                // 根据异常级别发送不同通知
                elevatorNotificationService.handleAbnormalityNotification(elevator, abnormality);
                
                // 对于严重异常，还需要通知业主
                String abnormalityLevel = abnormality.getAbnormalityLevel();
                if (AbnormalityLevelEnum.SERIOUS.getLevel().equals(abnormalityLevel)) {
                    String title = "电梯严重异常通知";
                    String content = "您所在楼栋" + elevator.getBuildingId() + "的电梯" + elevator.getElevatorNumber() 
                            + "出现严重异常，现已停止运行。请使用其他电梯或楼梯。\n异常类型："
                            + abnormality.getAbnormalityType();
                    
                    elevatorNotificationService.notifyPropertyOwners(elevator, title, content, true);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ElevatorAbnormalityDTO> listAllDTO() {
        List<ElevatorAbnormality> abnormalities = this.list();
        return convertToDTOList(abnormalities);
    }
    
    @Override
    public List<ElevatorAbnormalityDTO> listByElevatorIdDTO(Long elevatorId) {
        LambdaQueryWrapper<ElevatorAbnormality> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorAbnormality::getElevatorId, elevatorId);
        
        List<ElevatorAbnormality> abnormalities = this.list(queryWrapper);
        return convertToDTOList(abnormalities);
    }
    
    @Override
    public ElevatorAbnormalityDTO getDTOById(Long id) {
        ElevatorAbnormality abnormality = this.getById(id);
        return abnormality != null ? convertToDTO(abnormality) : null;
    }
    
    @Override
    public List<ElevatorAbnormality> listUnresolved() {
        LambdaQueryWrapper<ElevatorAbnormality> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PENDING.getStatus())
                .or()
                .eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PROCESSING.getStatus());
        return this.list(queryWrapper);
    }
    
    @Override
    public List<Object> getAbnormalityStats() {
        List<Object> stats = new ArrayList<>();
        Map<String, Object> statMap = new HashMap<>();
        
        // 统计各状态的异常数量
        LambdaQueryWrapper<ElevatorAbnormality> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PENDING.getStatus());
        long pendingCount = this.count(pendingQuery);
        
        LambdaQueryWrapper<ElevatorAbnormality> processingQuery = new LambdaQueryWrapper<>();
        processingQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PROCESSING.getStatus());
        long processingCount = this.count(processingQuery);
        
        LambdaQueryWrapper<ElevatorAbnormality> resolvedQuery = new LambdaQueryWrapper<>();
        resolvedQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.RESOLVED.getStatus());
        long resolvedCount = this.count(resolvedQuery);
        
        LambdaQueryWrapper<ElevatorAbnormality> closedQuery = new LambdaQueryWrapper<>();
        closedQuery.eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.CLOSED.getStatus());
        long closedCount = this.count(closedQuery);
        
        statMap.put("pending", pendingCount);
        statMap.put("processing", processingCount);
        statMap.put("resolved", resolvedCount);
        statMap.put("closed", closedCount);
        statMap.put("total", pendingCount + processingCount + resolvedCount + closedCount);
        
        stats.add(statMap);
        return stats;
    }
    
    @Override
    public List<ElevatorAbnormalityDTO> convertToDTOList(List<ElevatorAbnormality> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     */
    public ElevatorAbnormalityDTO convertToDTO(ElevatorAbnormality entity) {
        if (entity == null) {
            return null;
        }
        
        ElevatorAbnormalityDTO dto = new ElevatorAbnormalityDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // 如果需要额外的处理，例如获取电梯编号等
        if (entity.getElevatorId() != null) {
            Elevator elevator = elevatorService.getById(entity.getElevatorId());
            if (elevator != null) {
                dto.setElevatorNumber(elevator.getElevatorNumber().toString());
            }
        }
        
        return dto;
    }
} 