package com.mrdotxin.propsmart.urgent.service;

import com.mrdotxin.propsmart.urgent.model.entity.EmergencyContext;
import com.mrdotxin.propsmart.urgent.model.entity.EvacuationPoint;

import java.util.List;

public interface EmergencyDispatchService {

    /**
     *
     * @param context
     */
    void dispatchEmergency(EmergencyContext context);

    /**
     *
     * @return
     */
    List<EvacuationPoint> getSafeLocation();
}
