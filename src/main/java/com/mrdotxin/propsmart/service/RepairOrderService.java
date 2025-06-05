package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mrdotxin.propsmart.model.dto.repairOrder.RepairOrderQueryRequest;
import com.mrdotxin.propsmart.model.entity.RepairOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.vo.RepairOrderVO;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【repairorder(报修申请)】的数据库操作Service
* @createDate 2025-06-04 09:36:05
*/
public interface RepairOrderService extends IService<RepairOrder> {

    /**
     * 验证报修单参数合法性
     * @param repairOrder 报修单对象
     */
    void validateRepairOrder(RepairOrder repairOrder);

    /**
     * 获取查询条件包装器
     * @param queryRequest 查询请求
     * @return QueryWrapper
     */
    QueryWrapper<RepairOrder> getQueryWrapper(RepairOrderQueryRequest queryRequest);

    /**
     * 获取报修单VO对象
     * @param repairOrder 报修单实体
     * @return VO对象
     */
    RepairOrderVO getRepairOrderVO(RepairOrder repairOrder);

    /**
     * 获取报修单VO列表
     * @param repairOrderList 报修单实体列表
     * @return VO列表
     */
    List<RepairOrderVO> getRepairOrderVOList(List<RepairOrder> repairOrderList);

    /**
     * 获取报修单状态统计
     * @return 状态统计Map
     */
    Map<String, Long> getStatusStatistics();
}
