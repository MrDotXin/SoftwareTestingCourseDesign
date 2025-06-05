package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* 消防设备管理服务接口
* @author 32054
* @description 针对表【fireEquipment(消防设备综合管理表)】的数据库操作Service
* @createDate 2025-06-05 15:47:09
*/
public interface FireEquipmentService extends IService<FireEquipment> {

    /**
     * 获取需要巡检的设备列表（已过期或即将到期）
     * @param daysThreshold 即将到期的天数阈值（如3天内）
     * @return 需要巡检的设备列表
     */
    List<FireEquipment> getEquipmentNeedingInspection(int daysThreshold);
    
    /**
     * 获取指定楼栋的消防设备
     * @param buildingId 楼栋ID
     * @return 消防设备列表
     */
    List<FireEquipment> getEquipmentByBuilding(Long buildingId);
    
    /**
     * 进行消防设备巡检
     * @param equipmentId 设备ID
     * @param inspectorId 巡检人ID
     * @param remarks 巡检备注
     * @return 是否巡检成功
     */
    boolean performInspection(Long equipmentId, Long inspectorId, String remarks);
    
    /**
     * 更新设备状态
     * @param equipmentId 设备ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateEquipmentStatus(Long equipmentId, String status);
    
    /**
     * 按状态分页获取消防设备
     * @param status 设备状态
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<FireEquipment> pageByStatus(String status, long page, long size);
    
    /**
     * 设置下次巡检日期
     * @param equipmentId 设备ID
     * @param nextDate 下次巡检日期
     * @return 是否设置成功
     */
    boolean setNextInspectionDate(Long equipmentId, Date nextDate);
    
    /**
     * 统计指定状态的设备数量
     * @param status 设备状态
     * @return 设备数量
     */
    long countByStatus(String status);
}
