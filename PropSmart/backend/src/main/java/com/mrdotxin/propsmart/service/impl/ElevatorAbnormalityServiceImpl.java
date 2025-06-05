package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.ElevatorAbnormalityMapper;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.AbnormalityLevelEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import com.mrdotxin.propsmart.service.ElevatorNotificationService;
import com.mrdotxin.propsmart.service.ElevatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
} 