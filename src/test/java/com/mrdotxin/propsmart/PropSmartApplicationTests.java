package com.mrdotxin.propsmart;

import com.mrdotxin.propsmart.model.entity.FacilityReservation;
import com.mrdotxin.propsmart.service.FacilityReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class PropSmartApplicationTests {
    @Resource
    private FacilityReservationService facilityReservationService;
    @Test
    void contextLoads() {
    }

    @Test
    void test2() {

        FacilityReservation service = facilityReservationService.getById(1);

        service.getStatus().toString();
    }

}
