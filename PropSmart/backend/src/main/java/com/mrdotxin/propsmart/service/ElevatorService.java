package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorBasicInfoDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorDetailDTO;
import com.mrdotxin.propsmart.model.entity.Elevator;

import java.util.List;

/**
 * 电梯服务接口
 */
public interface ElevatorService extends IService<Elevator> {
    
    /**
     * 获取所有电梯基本信息（用户视图）
     * @return 电梯基本信息列表
     */
    List<ElevatorBasicInfoDTO> listElevatorBasicInfo();
    
    /**
     * 根据ID获取电梯基本信息（用户视图）
     * @param elevatorId 电梯ID
     * @return 电梯基本信息
     */
    ElevatorBasicInfoDTO getElevatorBasicInfo(Long elevatorId);
    
    /**
     * 获取所有电梯详细信息（管理员视图）
     * @return 电梯详细信息列表
     */
    List<ElevatorDetailDTO> listElevatorDetails();
    
    /**
     * 根据ID获取电梯详细信息（管理员视图）
     * @param elevatorId 电梯ID
     * @return 电梯详细信息
     */
    ElevatorDetailDTO getElevatorDetail(Long elevatorId);
    
    /**
     * 更新电梯维护日期
     * @param elevatorId 电梯ID
     * @return 是否更新成功
     */
    boolean updateMaintenanceDate(Long elevatorId);
    
    /**
     * 更新电梯运行状态
     * @param elevatorId 电梯ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateElevatorStatus(Long elevatorId, String status);
    
    /**
     * 启动电梯实时数据模拟器
     */
    void startElevatorDataSimulator();
    
    /**
     * 停止电梯实时数据模拟器
     */
    void stopElevatorDataSimulator();
}
