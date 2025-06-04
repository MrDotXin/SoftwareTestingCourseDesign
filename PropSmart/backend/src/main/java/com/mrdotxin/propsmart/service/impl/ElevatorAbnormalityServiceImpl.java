package com.mrdotxin.propsmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.mapper.ElevatorAbnormalityMapper;
import com.mrdotxin.propsmart.mapper.ElevatorMapper;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorAbnormalityDTO;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import com.mrdotxin.propsmart.service.ElevatorAbnormalityService;
import com.mrdotxin.propsmart.service.ElevatorService;
import com.mrdotxin.propsmart.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 电梯异常服务实现类
 */
@Service
public class ElevatorAbnormalityServiceImpl extends ServiceImpl<ElevatorAbnormalityMapper, ElevatorAbnormality> implements ElevatorAbnormalityService {

    @Resource
    private ElevatorMapper elevatorMapper;
    
    @Resource
    private UserService userService;
    
    @Resource
    private ElevatorService elevatorService;

    @Override
    public List<ElevatorAbnormalityDTO> listAbnormalities() {
        List<ElevatorAbnormality> abnormalities = list(new QueryWrapper<ElevatorAbnormality>().orderByDesc("occurrenceTime"));
        return convertToDTOList(abnormalities);
    }

    @Override
    public List<ElevatorAbnormalityDTO> listAbnormalitiesByElevatorId(Long elevatorId) {
        List<ElevatorAbnormality> abnormalities = list(
                new QueryWrapper<ElevatorAbnormality>()
                        .eq("elevatorId", elevatorId)
                        .orderByDesc("occurrenceTime")
        );
        return convertToDTOList(abnormalities);
    }

    @Override
    public Long createAbnormality(ElevatorAbnormality abnormality) {
        // 设置默认值
        if (abnormality.getOccurrenceTime() == null) {
            abnormality.setOccurrenceTime(new Date());
        }
        if (abnormality.getStatus() == null) {
            abnormality.setStatus(AbnormalityStatusEnum.PENDING.getStatus());
        }
        abnormality.setCreateTime(new Date());
        abnormality.setUpdateTime(new Date());
        
        save(abnormality);
        return abnormality.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAbnormality(Long abnormalityId, Long handlerId, String status, String handlingNotes) {
        ElevatorAbnormality abnormality = getById(abnormalityId);
        if (abnormality == null) {
            return false;
        }
        
        abnormality.setHandlerId(handlerId);
        abnormality.setStatus(status);
        abnormality.setHandlingNotes(handlingNotes);
        abnormality.setUpdateTime(new Date());
        
        // 如果状态是已解决，设置恢复时间
        if (status.equals(AbnormalityStatusEnum.RESOLVED.getStatus())) {
            abnormality.setRecoveryTime(new Date());
            
            // 更新电梯状态为正常
            Elevator elevator = elevatorMapper.selectById(abnormality.getElevatorId());
            if (elevator != null) {
                elevator.setCurrentStatus(ElevatorStatusEnum.NORMAL.getStatus());
                elevatorMapper.updateById(elevator);
            }
        }
        
        return updateById(abnormality);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeAbnormality(Long abnormalityId, Long handlerId) {
        return handleAbnormality(abnormalityId, handlerId, AbnormalityStatusEnum.CLOSED.getStatus(), "异常已关闭");
    }
    
    /**
     * 将异常实体列表转换为DTO列表
     */
    private List<ElevatorAbnormalityDTO> convertToDTOList(List<ElevatorAbnormality> abnormalities) {
        if (abnormalities == null || abnormalities.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有相关的电梯信息
        List<Long> elevatorIds = abnormalities.stream()
                .map(ElevatorAbnormality::getElevatorId)
                .distinct()
                .collect(Collectors.toList());
        
        List<Elevator> elevators = elevatorMapper.selectBatchIds(elevatorIds);
        Map<Long, Elevator> elevatorMap = elevators.stream()
                .collect(Collectors.toMap(Elevator::getId, Function.identity()));
        
        // 获取所有处理人信息
        List<Long> handlerIds = abnormalities.stream()
                .map(ElevatorAbnormality::getHandlerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> handlerNameMap = new java.util.HashMap<>();
        if (!handlerIds.isEmpty()) {
            List<User> handlers = userService.listByIds(handlerIds);
            handlerNameMap = handlers.stream()
                    .collect(Collectors.toMap(User::getId, User::getUserRealName));
        }
        
        // 转换为DTO
        List<ElevatorAbnormalityDTO> dtoList = new ArrayList<>();
        for (ElevatorAbnormality abnormality : abnormalities) {
            ElevatorAbnormalityDTO dto = new ElevatorAbnormalityDTO();
            BeanUtils.copyProperties(abnormality, dto);
            
            // 设置电梯编号
            Elevator elevator = elevatorMap.get(abnormality.getElevatorId());
            if (elevator != null) {
                dto.setElevatorNumber(elevator.getElevatorNumber());
            }
            
            // 设置处理人姓名
            if (abnormality.getHandlerId() != null) {
                dto.setHandlerName(handlerNameMap.get(abnormality.getHandlerId()));
            }
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
}




