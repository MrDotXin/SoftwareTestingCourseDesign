package com.mrdotxin.propsmart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.propsmart.common.ErrorCode;
import com.mrdotxin.propsmart.constant.CommonConstant;
import com.mrdotxin.propsmart.exception.BusinessException;
import com.mrdotxin.propsmart.exception.ThrowUtils;
import com.mrdotxin.propsmart.mapper.EnergyConsumptionMapper;
import com.mrdotxin.propsmart.model.dto.energyConsumption.EnergyConsumptionQueryRequest;
import com.mrdotxin.propsmart.model.entity.Building;
import com.mrdotxin.propsmart.model.entity.EnergyConsumption;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.vo.EnergyConsumptionVO;
import com.mrdotxin.propsmart.model.vo.EnergyMonthlyStatsVO;
import com.mrdotxin.propsmart.service.BuildingService;
import com.mrdotxin.propsmart.service.EnergyConsumptionService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.utils.SqlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【energyconsumption(账单流水)】的数据库操作Service实现
* @createDate 2025-06-05 11:07:06
*/
@Service
public class EnergyConsumptionServiceImpl extends ServiceImpl<EnergyConsumptionMapper, EnergyConsumption>
        implements EnergyConsumptionService {

    @Resource
    private PropertyService propertyService;

    @Resource
    private BuildingService buildingService;

    @Override
    public void validateEnergyConsumption(EnergyConsumption energy) {
        ThrowUtils.throwIf(ObjectUtil.isNull(energy), ErrorCode.PARAMS_ERROR);

        Long propertyId = energy.getPropertyId();
        String energyType = energy.getEnergyType();
        Double consumption = energy.getConsumption();
        Double price = energy.getPrice();
        Date measureTime = energy.getMeasureTime();

        // 基本参数校验
        ThrowUtils.throwIf(ObjectUtil.isNull(propertyId), ErrorCode.PARAMS_ERROR, "房产ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(energyType), ErrorCode.PARAMS_ERROR, "能耗类型不能为空");
        ThrowUtils.throwIf(ObjectUtil.isNull(consumption), ErrorCode.PARAMS_ERROR, "消耗值不能为空");
        ThrowUtils.throwIf(ObjectUtil.isNull(price), ErrorCode.PARAMS_ERROR, "价格不能为空");
        ThrowUtils.throwIf(ObjectUtil.isNull(measureTime), ErrorCode.PARAMS_ERROR, "测量时间不能为空");

        // 数值校验
        ThrowUtils.throwIf(consumption < 0, ErrorCode.PARAMS_ERROR, "消耗值不能为负数");
        ThrowUtils.throwIf(price < 0, ErrorCode.PARAMS_ERROR, "价格不能为负数");

        // 验证房产是否存在
        Property property = propertyService.getById(propertyId);
        ThrowUtils.throwIf(ObjectUtil.isNull(property), ErrorCode.NOT_FOUND_ERROR, "房产不存在");
    }

    @Override
    public QueryWrapper<EnergyConsumption> getQueryWrapper(EnergyConsumptionQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = queryRequest.getId();
        Long propertyId = queryRequest.getPropertyId();
        String energyType = queryRequest.getEnergyType();
        Double minConsumption = queryRequest.getMinConsumption();
        Double maxConsumption = queryRequest.getMaxConsumption();
        Double minPrice = queryRequest.getMinPrice();
        Double maxPrice = queryRequest.getMaxPrice();
        Date measureStart = queryRequest.getMeasureStart();
        Date measureEnd = queryRequest.getMeasureEnd();
        Date createStart = queryRequest.getCreateStart();
        Date createEnd = queryRequest.getCreateEnd();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        QueryWrapper<EnergyConsumption> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id), "id", id);
        queryWrapper.eq(ObjectUtil.isNotNull(propertyId), "propertyId", propertyId);
        queryWrapper.eq(StrUtil.isNotBlank(energyType), "energyType", energyType);

        // 消耗值范围查询
        if (ObjectUtil.isNotNull(minConsumption)) {
            queryWrapper.ge("consumption", minConsumption);
        }
        if (ObjectUtil.isNotNull(maxConsumption)) {
            queryWrapper.le("consumption", maxConsumption);
        }

        // 价格范围查询
        if (ObjectUtil.isNotNull(minPrice)) {
            queryWrapper.ge("price", minPrice);
        }
        if (ObjectUtil.isNotNull(maxPrice)) {
            queryWrapper.le("price", maxPrice);
        }

        // 测量时间范围查询
        if (ObjectUtil.isNotNull(measureStart)) {
            queryWrapper.ge("measureTime", measureStart);
        }
        if (ObjectUtil.isNotNull(measureEnd)) {
            queryWrapper.le("measureTime", measureEnd);
        }

        // 创建时间范围查询
        if (ObjectUtil.isNotNull(createStart)) {
            queryWrapper.ge("createTime", createStart);
        }
        if (ObjectUtil.isNotNull(createEnd)) {
            queryWrapper.le("createTime", createEnd);
        }

        // 排序处理
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                          sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                          sortField);

        return queryWrapper;
    }

    @Override
    public EnergyConsumptionVO getEnergyConsumptionVO(EnergyConsumption energy) {
        if (energy == null) {
            return null;
        }

        EnergyConsumptionVO vo = new EnergyConsumptionVO();
        BeanUtils.copyProperties(energy, vo);

        // 查询关联的房产信息
        Property property = propertyService.getById(energy.getPropertyId());
        if (property != null) {
            vo.setUnitNumber(property.getUnitNumber());
            vo.setRoomNumber(property.getRoomNumber());

            // 查询楼栋信息
            Building building = buildingService.getById(property.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getBuildingName());
            }
        }

        // 计算总费用
        vo.setTotalCost(energy.getConsumption() * energy.getPrice());

        return vo;
    }

    @Override
    public List<EnergyConsumptionVO> getEnergyConsumptionVOList(List<EnergyConsumption> energyList) {
        if (CollUtil.isEmpty(energyList)) {
            return new ArrayList<>();
        }
        return energyList.stream().map(this::getEnergyConsumptionVO).collect(Collectors.toList());
    }

    @Override
    public List<EnergyMonthlyStatsVO> getMonthlyStats(Long propertyId, String energyType, String yearMonth) {
        // 验证年月格式
        if (!ReUtil.isMatch("^\\d{4}-\\d{2}$", yearMonth)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "年月格式不正确，应为yyyy-MM");
        }

        // 构建查询条件
        QueryWrapper<EnergyConsumption> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("energyType", energyType);
        queryWrapper.apply("DATE_FORMAT(measureTime,'%Y-%m') = {0}", yearMonth);

        if (ObjectUtil.isNotNull(propertyId)) {
            queryWrapper.eq("propertyId", propertyId);
        }

        // 按天分组统计
        queryWrapper.select(
            "DAY(measureTime) as day",
            "SUM(consumption) as totalConsumption",
            "SUM(consumption * price) as totalCost"
        );
        queryWrapper.groupBy("DAY(measureTime)");
        queryWrapper.orderByAsc("DAY(measureTime)");

        return this.baseMapper.selectMaps(queryWrapper).stream()
            .map(map -> {
                EnergyMonthlyStatsVO vo = new EnergyMonthlyStatsVO();
                vo.setDay((Integer) map.get("day"));
                vo.setTotalConsumption((Double) map.get("totalConsumption"));
                vo.setTotalCost((Double) map.get("totalCost"));
                return vo;
            })
            .collect(Collectors.toList());
    }

    @Override
    public double getAverageConsumption(Long propertyId, String energyType, Date startTime, Date endTime) {
        // 构建查询条件
        QueryWrapper<EnergyConsumption> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("AVG(consumption) as avgConsumption")
                   .eq("propertyId", propertyId)
                   .eq("energyType", energyType)
                   .between("measureTime", startTime, endTime);

        // 执行查询
        Map<String, Object> result = this.getMap(queryWrapper);

        // 处理结果
        if (result == null || result.get("avgConsumption") == null) {
            return 0.0;
        }

        return ((Number) result.get("avgConsumption")).doubleValue();
    }

    @Override
    public Double getTotalConsumption(Long propertyId, String energyType, Date startTime, Date endTime) {
        QueryWrapper<EnergyConsumption> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(consumption) as totalConsumption")
                   .eq("propertyId", propertyId)
                   .eq("energyType", energyType)
                   .between("measureTime", startTime, endTime);

        Map<String, Object> result = this.getMap(queryWrapper);

        if (result == null || result.get("totalConsumption") == null) {
            return 0.0;
        }

        return ((Number) result.get("totalConsumption")).doubleValue();
    }
}




