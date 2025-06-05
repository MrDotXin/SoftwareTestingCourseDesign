package com.mrdotxin.propsmart.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.mrdotxin.propsmart.model.entity.EnergyConsumption;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.EnergyConsumptionService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.websocket.NotificationService;
import com.mrdotxin.propsmart.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class EnergyConsumptionTask {

    @Resource
    private EnergyConsumptionService energyConsumptionService;

    @Resource
    private NotificationService notificationService;

    @Resource
    private PropertyService propertyService;

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserService userService;

    // 每小时执行一次，模拟能耗数据
    @Scheduled(cron = "${Energy-Consumption-Generate-Interval}")
    public void generateHourlyEnergyConsumption() {
        log.info("开始生成每小时能耗数据...");

        List<Property> properties = propertyService.list();
        if (CollUtil.isEmpty(properties)) {
            return;
        }

        int generatedCount = 0;
        for (Property property : properties) {
            if (ObjectUtils.isNotEmpty(property.getOwnerIdentity())) {
                try {
                    // 生成电力消耗
                    generateEnergyRecord(property, "electricity");
                    // 生成水消耗
                    generateEnergyRecord(property, "water");
                    generatedCount++;
                } catch (Exception e) {
                    log.error("生成房产{}能耗数据失败: {}", property.getId(), e.getMessage());
                }
            }
        }

        log.info("能耗数据生成完成，共生成{}条记录", generatedCount * 2);
    }

    private void generateEnergyRecord(Property property, String energyType) {
        EnergyConsumption record = new EnergyConsumption();
        record.setPropertyId(property.getId());
        record.setEnergyType(energyType);

        // 基础消耗量 + 随机波动
        double baseConsumption = energyType.equals("electricity") ? 5.0 : 2.0;
        double randomFactor = 0.5 + new Random().nextDouble();
        double consumption = baseConsumption * randomFactor;

        // 特殊事件：10%概率产生高消耗
        if (new Random().nextDouble() < 0.1) {
            consumption *= 3.0;
        }

        record.setConsumption(consumption);
        record.setPrice(energyType.equals("electricity") ? 0.8 : 3.5);
        record.setMeasureTime(new Date());

        energyConsumptionService.save(record);

        // 检查异常能耗
        checkAbnormalConsumption(property, energyType, consumption);
    }

    private void checkAbnormalConsumption(Property property, String energyType, double currentConsumption) {
        // 获取过去24小时的平均消耗
        Date endTime = new Date();
        Date startTime = DateUtil.offsetHour(endTime, -24);

        double avgConsumption = energyConsumptionService.getAverageConsumption(
            property.getId(), energyType, startTime, endTime);

        // 如果当前消耗是平均值的2倍以上，视为异常
        if (avgConsumption > 0 && currentConsumption > avgConsumption * 2) {
            notifyAbnormalConsumption(property, energyType, currentConsumption, avgConsumption);
        }
    }

    private void notifyAbnormalConsumption(Property property, String energyType,
                                         double current, double average) {
        User user = userService.getByFiled("userIdCardNumber", property.getOwnerIdentity());
        Long ownerId = user.getId();

        String energyName = energyType.equals("electricity") ? "电力" : "水";
        String message = String.format(
            "您的房产(%s)检测到异常%s消耗！当前值: %.2f，过去24小时平均值: %.2f",
            property.getRoomNumber(), energyName, current, average);

            try {
                notificationService.handleAbnormalEnergyConsumptionNotification(user, property, message);
            } catch (Exception e) {
                log.error("发送异常能耗通知失败，用户ID: {}", ownerId, e);
            }
        }
}