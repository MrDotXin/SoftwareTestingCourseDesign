package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.job.ElevatorDataSimulatorJob;
import com.mrdotxin.propsmart.mapper.ElevatorMapper;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorBasicInfoDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorDetailDTO;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityResolverService;
import com.mrdotxin.propsmart.service.ElevatorNotificationService;
import com.mrdotxin.propsmart.service.ElevatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 电梯服务实现类
 */
@Slf4j
@Service
public class ElevatorServiceImpl extends ServiceImpl<ElevatorMapper, Elevator> implements ElevatorService {

    @Resource
    private ElevatorDataSimulatorJob elevatorDataSimulatorJob;

    @Resource
    private ElevatorAbnormalityResolverService elevatorAbnormalityResolverService;
    
    @Resource
    private ElevatorNotificationService elevatorNotificationService;

    @Override
    public boolean save(Elevator elevator) {
        // 设置创建和更新时间
        if (elevator.getCreateTime() == null) {
            elevator.setCreateTime(new Date());
        }
        if (elevator.getUpdateTime() == null) {
            elevator.setUpdateTime(new Date());
        }
        return super.save(elevator);
    }

    @Override
    public boolean updateById(Elevator elevator) {
        // 获取更新前的电梯数据
        Elevator oldElevator = this.getById(elevator.getId());
        
        // 更新时间
        elevator.setUpdateTime(new Date());
        boolean result = super.updateById(elevator);
        
        // 如果状态发生变化，发送通知
        if (oldElevator != null && !oldElevator.getCurrentStatus().equals(elevator.getCurrentStatus())) {
            elevatorNotificationService.handleStatusChangeNotification(
                    elevator, oldElevator.getCurrentStatus(), elevator.getCurrentStatus());
        }
        
        return result;
    }

    @Override
    public List<ElevatorBasicInfoDTO> listElevatorBasicInfo() {
        // 查询所有电梯
        List<Elevator> elevatorList = this.list();
        List<ElevatorBasicInfoDTO> resultList = new ArrayList<>();
        
        // 转换为DTO
        for (Elevator elevator : elevatorList) {
            ElevatorBasicInfoDTO dto = new ElevatorBasicInfoDTO();
            BeanUtils.copyProperties(elevator, dto);
            resultList.add(dto);
        }
        
        return resultList;
    }

    @Override
    public List<ElevatorDetailDTO> listElevatorDetails() {
        // 查询所有电梯
        List<Elevator> elevatorList = this.list();
        List<ElevatorDetailDTO> resultList = new ArrayList<>();
        
        // 转换为DTO
        for (Elevator elevator : elevatorList) {
            ElevatorDetailDTO dto = new ElevatorDetailDTO();
            BeanUtils.copyProperties(elevator, dto);
            resultList.add(dto);
        }
        
        return resultList;
    }

    @Override
    public ElevatorDetailDTO getElevatorDetail(Long elevatorId) {
        // 查询电梯
        Elevator elevator = this.getById(elevatorId);
        if (elevator == null) {
            return null;
        }
        
        // 转换为DTO
        ElevatorDetailDTO dto = new ElevatorDetailDTO();
        BeanUtils.copyProperties(elevator, dto);
        
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaintenanceDate(Long elevatorId) {
        // 查询电梯
        Elevator elevator = this.getById(elevatorId);
        if (elevator == null) {
            return false;
        }
        
        try {
            String prevStatus = elevator.getCurrentStatus();
            
            // 更新电梯维护日期
            elevator.setLastMaintenanceDate(new Date());
            // 将电梯状态改为正常
            elevator.setCurrentStatus(ElevatorStatusEnum.NORMAL.getStatus());
            this.updateById(elevator);
            
            // 发送状态变更通知
            if (!ElevatorStatusEnum.NORMAL.getStatus().equals(prevStatus)) {
                elevatorNotificationService.handleStatusChangeNotification(
                        elevator, prevStatus, elevator.getCurrentStatus());
            }
            
            // 自动解决该电梯的所有未解决异常
            List<ElevatorAbnormality> abnormalityList = elevatorAbnormalityResolverService.resolveElevatorAbnormalities(elevatorId);
            
            if (!abnormalityList.isEmpty()) {
                elevatorAbnormalityResolverService.updateBatchAbnormalities(abnormalityList);
            }
            
            // 发送维护完成通知
            elevatorNotificationService.handleMaintenanceCompletedNotification(elevator, abnormalityList.size());
            
            log.info("电梯 {} 维护完成，已自动解决 {} 个异常", elevatorId, abnormalityList.size());
            return true;
        } catch (Exception e) {
            log.error("更新电梯维护日期失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void startElevatorDataSimulator() {
        elevatorDataSimulatorJob.startSimulator();
    }

    @Override
    public void stopElevatorDataSimulator() {
        elevatorDataSimulatorJob.stopSimulator();
    }
} 