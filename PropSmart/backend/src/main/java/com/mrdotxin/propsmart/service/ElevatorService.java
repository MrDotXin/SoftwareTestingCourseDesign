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
     * 保存电梯基本信息
     * @param elevator 电梯实体
     * @return 是否保存成功
     */
    boolean save(Elevator elevator);
    
    /**
     * 更新电梯信息
     * @param elevator 电梯实体
     * @return 是否更新成功
     */
    boolean updateById(Elevator elevator);
    
    /**
     * 获取所有电梯的基本信息（用户视图）
     * @return 电梯基本信息列表
     */
    List<ElevatorBasicInfoDTO> listElevatorBasicInfo();
    
    /**
     * 获取所有电梯的详细信息（管理员视图）
     * @return 电梯详细信息列表
     */
    List<ElevatorDetailDTO> listElevatorDetails();
    
    /**
     * 获取单个电梯的详细信息（管理员视图）
     * @param elevatorId 电梯ID
     * @return 电梯详细信息
     */
    ElevatorDetailDTO getElevatorDetail(Long elevatorId);
    
    /**
     * 更新电梯的维护日期（设为当前日期）
     * @param elevatorId 电梯ID
     * @return 是否更新成功
     */
    boolean updateMaintenanceDate(Long elevatorId);
    
    /**
     * 启动电梯数据模拟器
     */
    void startElevatorDataSimulator();
    
    /**
     * 停止电梯数据模拟器
     */
    void stopElevatorDataSimulator();
} 