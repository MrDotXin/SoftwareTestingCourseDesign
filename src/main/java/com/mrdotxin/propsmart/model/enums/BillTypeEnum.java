package com.mrdotxin.propsmart.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账单类型枚举
 */
public enum BillTypeEnum {

    PROPERTY_FEE("property_fee", "物业费"),
    WATER("water", "水费"),
    ELECTRICITY("electricity", "电费"),
    OTHER("other", "其他费用");

    private final String value;

    private final String text;

    BillTypeEnum(String value, String text) {
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