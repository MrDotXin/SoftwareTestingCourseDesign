package com.mrdotxin.propsmart.model.enums;

import lombok.Getter;

@Getter
public enum RepairOrderStatusEnum {

    PENDING("pending", "待处理"),
    PROCESSING("processing", "处理中"),
    COMPLETED("completed", "已完成"),
    CANCELLED("cancelled", "已取消");

    private final String value;
    private final String text;

    RepairOrderStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public static String getTextByValue(String value) {
        for (RepairOrderStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status.text;
            }
        }
        return "未知状态";
    }
}
