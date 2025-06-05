package com.mrdotxin.propsmart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mrdotxin.propsmart.model.dto.energyConsumption.EnergyConsumptionQueryRequest;
import com.mrdotxin.propsmart.model.entity.EnergyConsumption;
import com.mrdotxin.propsmart.model.vo.EnergyConsumptionVO;
import com.mrdotxin.propsmart.model.vo.EnergyMonthlyStatsVO;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
*/
public interface EnergyConsumptionService extends IService<EnergyConsumption> {

    /**
     * 验证能耗记录参数合法性
     * @param energyConsumption 能耗记录对象
     */
    void validateEnergyConsumption(EnergyConsumption energyConsumption);

    /**
     * 获取查询条件包装器
     * @param queryRequest 查询请求
     * @return QueryWrapper
     */
    QueryWrapper<EnergyConsumption> getQueryWrapper(EnergyConsumptionQueryRequest queryRequest);

    /**
     * 获取能耗记录VO对象
     * @param energyConsumption 能耗记录实体
     * @return VO对象
     */
    EnergyConsumptionVO getEnergyConsumptionVO(EnergyConsumption energyConsumption);

    /**
     * 获取能耗记录VO列表
     * @param energyConsumptionList 能耗记录实体列表
     * @return VO列表
     */
    List<EnergyConsumptionVO> getEnergyConsumptionVOList(List<EnergyConsumption> energyConsumptionList);

    /**
     * 按月统计能耗数据
     * @param propertyId 房产ID（可选）
     * @param energyType 能耗类型
     * @param yearMonth 年月（格式：yyyy-MM）
     * @return 统计结果列表
     */
    List<EnergyMonthlyStatsVO> getMonthlyStats(Long propertyId, String energyType, String yearMonth);

    /**
     *
     * @param propertyId
     * @param energyType
     * @param startTime
     * @param endTime
     * @return
     */
    double getAverageConsumption(Long propertyId, String energyType, Date startTime, Date endTime);

    /**
     *
      * @param propertyId
     * @param energyType
     * @param startTime
     * @param endTime
     * @return
     */
    Double getTotalConsumption(Long propertyId, String energyType, Date startTime, Date endTime);
}
