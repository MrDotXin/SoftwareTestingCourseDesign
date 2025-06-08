package com.mrdotxin.propsmart.service.impl.elevator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mrdotxin.propsmart.mapper.mysql.ElevatorAbnormalityMapper;
import com.mrdotxin.propsmart.model.entity.ElevatorAbnormality;
import com.mrdotxin.propsmart.model.enums.AbnormalityStatusEnum;
import com.mrdotxin.propsmart.service.elevator.ElevatorAbnormalityResolverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 电梯异常解决服务实现类
 */
@Slf4j
@Service
public class ElevatorAbnormalityResolverServiceImpl implements ElevatorAbnormalityResolverService {

    @Resource
    private ElevatorAbnormalityMapper elevatorAbnormalityMapper;

    @Override
    public List<ElevatorAbnormality> resolveElevatorAbnormalities(Long elevatorId) {
        // 查询指定电梯的未解决异常
        LambdaQueryWrapper<ElevatorAbnormality> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ElevatorAbnormality::getElevatorId, elevatorId)
                .and(wrapper -> wrapper
                        .eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PENDING.getStatus())
                        .or()
                        .eq(ElevatorAbnormality::getStatus, AbnormalityStatusEnum.PROCESSING.getStatus())
                );
                
        List<ElevatorAbnormality> abnormalityList = elevatorAbnormalityMapper.selectList(queryWrapper);
        
        // 更新异常状态为已解决
        for (ElevatorAbnormality abnormality : abnormalityList) {
            abnormality.setStatus(AbnormalityStatusEnum.RESOLVED.getStatus());
            abnormality.setRecoveryTime(new Date());
            abnormality.setHandlingNotes((abnormality.getHandlingNotes() == null ? "" : abnormality.getHandlingNotes() + "\n") + "通过维护解决");
        }
        
        return abnormalityList;
    }
    
    @Override
    public void updateBatchAbnormalities(List<ElevatorAbnormality> abnormalities) {
        if (abnormalities == null || abnormalities.isEmpty()) {
            return;
        }
        
        for (ElevatorAbnormality abnormality : abnormalities) {
            elevatorAbnormalityMapper.updateById(abnormality);
        }
    }
} 