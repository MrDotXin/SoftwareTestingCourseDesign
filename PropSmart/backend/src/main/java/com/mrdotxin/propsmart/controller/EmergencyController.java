package com.mrdotxin.propsmart.controller;

import com.mrdotxin.propsmart.annotation.AuthCheck;
import com.mrdotxin.propsmart.common.BaseResponse;
import com.mrdotxin.propsmart.common.ResultUtils;
import com.mrdotxin.propsmart.constant.UserConstant;
import com.mrdotxin.propsmart.urgent.model.dto.EmergencyAlertRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emergency")
public class EmergencyController {

    @PostMapping("/alert")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> alertEmergency(@RequestBody  EmergencyAlertRequest emergencyAlertRequest) {
        return ResultUtils.success(true);
    }

}
