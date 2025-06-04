package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentAddRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentQueryRequest;
import com.mrdotxin.propsmart.model.dto.fireequipment.FireEquipmentUpdateRequest;
import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.model.entity.User;

import java.util.List;

/**
 * 消防设备服务
 */
public interface FireEquipmentService extends IService<FireEquipment> {

    /**
     * 添加消防设备
     * 
     * @param fireEquipmentAddRequest
     * @param loginUser
     * @return
     */
    Long addFireEquipment(FireEquipmentAddRequest fireEquipmentAddRequest, User loginUser);

    /**
     * 更新消防设备
     *
     * @param fireEquipmentUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateFireEquipment(FireEquipmentUpdateRequest fireEquipmentUpdateRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param fireEquipmentQueryRequest
     * @return
     */
    QueryWrapper<FireEquipment> getQueryWrapper(FireEquipmentQueryRequest fireEquipmentQueryRequest);

    /**
     * 获取待巡检的设备列表
     *
     * @return
     */
    List<FireEquipment> getPendingInspectionEquipment();
}
