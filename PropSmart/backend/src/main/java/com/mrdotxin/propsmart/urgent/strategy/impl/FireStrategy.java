package com.mrdotxin.propsmart.urgent.strategy.impl;

import com.mrdotxin.propsmart.model.entity.FireEquipment;
import com.mrdotxin.propsmart.service.FireEquipmentService;
import com.mrdotxin.propsmart.urgent.model.enums.EmergencyTypeEnum;
import com.mrdotxin.propsmart.urgent.model.entity.EmergencyContext;
import com.mrdotxin.propsmart.urgent.model.entity.EmergencyLocation;
import com.mrdotxin.propsmart.urgent.model.entity.EvacuationPoint;
import com.mrdotxin.propsmart.urgent.strategy.EmergencyStrategy;
import com.mrdotxin.propsmart.websocket.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component("fireStrategy")
public class FireStrategy implements EmergencyStrategy {
    @Resource
    private NotificationService notificationService;

    @Resource
    private FireEquipmentService fireEquipmentService;

    @Override
    public void startEmergencyPlan(EmergencyContext context) {
        notificationService.handleUrgentEmergencyNotification(
                "小区出现火灾 分别位于" + StringUtils.join(
                        context.getDisasterLocation().stream().map(EmergencyLocation::getLocationDesc)
                                .collect(Collectors.toList()), " "
                ),
                EmergencyTypeEnum.FIRE,
                true
        );
    }

    @Override
    public void planEvacuationRoutes(EmergencyContext context) {
        notificationService.handleUrgentEmergencyNotification(
            "请前往最近的疏离点 !" + StringUtils.join(
                    context.getEvacuationPoints().stream().map(EvacuationPoint::getDescription)
                            .collect(Collectors.toList()), " "
            ),
            EmergencyTypeEnum.FIRE,
        true
        );
    }

    @Override
    public void allocateResources(EmergencyContext context) {
        List<FireEquipment>  list = fireEquipmentService.list();
        notificationService.handleUrgentEmergencyNotification(
            "请前往最近的疏离点 !" + StringUtils.join(
                    context.getEvacuationPoints().stream().map(EvacuationPoint::getDescription)
                            .collect(Collectors.toList()), " "
            ),
            EmergencyTypeEnum.FIRE,
        true
        );
    }
}
