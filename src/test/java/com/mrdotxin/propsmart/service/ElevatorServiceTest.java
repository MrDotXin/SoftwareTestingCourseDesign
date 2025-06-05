package com.mrdotxin.propsmart.service;

import com.mrdotxin.propsmart.model.dto.elevator.ElevatorBasicInfoDTO;
import com.mrdotxin.propsmart.model.dto.elevator.ElevatorDetailDTO;
import com.mrdotxin.propsmart.model.entity.Elevator;
import com.mrdotxin.propsmart.model.enums.ElevatorDirectionEnum;
import com.mrdotxin.propsmart.model.enums.ElevatorStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 电梯服务测试类
 */
@SpringBootTest
public class ElevatorServiceTest {
    
    @Resource
    private ElevatorService elevatorService;
    
    @Resource
    private ElevatorConfigService elevatorConfigService;
    
    /**
     * 测试创建测试电梯数据
     */
    @Test
    public void testCreateTestData() {
        // 创建测试电梯数据
        Elevator elevator = new Elevator();
        elevator.setBuildingId(1L);
        elevator.setElevatorNumber("A栋1号电梯");
        elevator.setInstallationDate(new Date());
        elevator.setLastMaintenanceDate(new Date());
        elevator.setCurrentStatus(ElevatorStatusEnum.NORMAL.getStatus());
        elevator.setCurrentFloor(1);
        elevator.setRunningDirection(ElevatorDirectionEnum.STATIONARY.getDirection());
        elevator.setLoadPercentage(0);
        elevator.setDoorStatus("关闭");
        elevator.setCabinTemperature(new BigDecimal("25.0"));
        elevator.setMotorTemperature(new BigDecimal("40.0"));
        elevator.setRunningSpeed(new BigDecimal("0.0"));
        elevator.setRatedSpeed(new BigDecimal("2.0"));
        elevator.setAcceleration(new BigDecimal("0.0"));
        elevator.setPowerConsumption(new BigDecimal("0.5"));
        elevator.setCreateTime(new Date());
        elevator.setUpdateTime(new Date());
        
        boolean result = elevatorService.save(elevator);
        assertTrue(result);
        
        // 为新电梯创建默认配置
        boolean configResult = elevatorConfigService.createDefaultConfig(elevator.getId());
        assertTrue(configResult);
    }
    
    /**
     * 测试电梯数据模拟器
     */
    @Test
    public void testElevatorDataSimulator() throws InterruptedException {
        // 启动模拟器
        elevatorService.startElevatorDataSimulator();
        
        // 等待一段时间，让模拟器生成一些数据
        Thread.sleep(10000);
        
        // 获取电梯详细信息
        List<ElevatorDetailDTO> elevators = elevatorService.listElevatorDetails();
        assertFalse(elevators.isEmpty());
        
        // 打印电梯信息
        for (ElevatorDetailDTO elevator : elevators) {
            System.out.println("电梯ID: " + elevator.getId());
            System.out.println("电梯编号: " + elevator.getElevatorNumber());
            System.out.println("当前状态: " + elevator.getCurrentStatus());
            System.out.println("当前楼层: " + elevator.getCurrentFloor());
            System.out.println("运行方向: " + elevator.getRunningDirection());
            System.out.println("轿厢温度: " + elevator.getCabinTemperature() + "℃");
            System.out.println("电机温度: " + elevator.getMotorTemperature() + "℃");
            System.out.println("运行速度: " + elevator.getRunningSpeed() + " m/s");
            System.out.println("负载百分比: " + elevator.getLoadPercentage() + "%");
            System.out.println("电梯门状态: " + elevator.getDoorStatus());
            System.out.println("-------------------------------");
        }
        
        // 停止模拟器
        elevatorService.stopElevatorDataSimulator();
    }
    
    /**
     * 测试电梯基本信息查询（用户视图）
     */
    @Test
    public void testListElevatorBasicInfo() {
        List<ElevatorBasicInfoDTO> elevators = elevatorService.listElevatorBasicInfo();
        assertFalse(elevators.isEmpty());
        
        // 验证用户视图只包含基本信息
        ElevatorBasicInfoDTO elevator = elevators.get(0);
        assertNotNull(elevator.getId());
        assertNotNull(elevator.getElevatorNumber());
        assertNotNull(elevator.getCurrentStatus());
    }
    
    /**
     * 测试更新电梯维护日期
     */
    @Test
    public void testUpdateMaintenanceDate() {
        // 获取第一个电梯
        List<ElevatorDetailDTO> elevators = elevatorService.listElevatorDetails();
        if (elevators.isEmpty()) {
            return;
        }
        
        Long elevatorId = elevators.get(0).getId();
        boolean result = elevatorService.updateMaintenanceDate(elevatorId);
        assertTrue(result);
        
        // 验证维护日期已更新且状态变为正常
        ElevatorDetailDTO updatedElevator = elevatorService.getElevatorDetail(elevatorId);
        assertNotNull(updatedElevator.getLastMaintenanceDate());
        assertEquals(ElevatorStatusEnum.NORMAL.getStatus(), updatedElevator.getCurrentStatus());
    }
}