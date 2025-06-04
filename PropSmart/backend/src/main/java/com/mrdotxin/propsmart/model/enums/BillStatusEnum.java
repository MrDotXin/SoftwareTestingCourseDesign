package com.mrdotxin.propsmart.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账单状态枚举
 */
public enum BillStatusEnum {

    UNPAID("unpaid", "未缴费"),
    PAID("paid", "已缴费"),
    OVERDUE("overdue", "已逾期");

    private final String value;

    private final String text;

    BillStatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 获取标签列表
     *
     * @return
     */
    public static List<String> getTexts() {
        return Arrays.stream(values()).map(item -> item.text).collect(Collectors.toList());
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
} 