package com.mrdotxin.propsmart.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ComplaintTypeEnum {
    COMPLAINT("complaint", "投诉"),
    SUGGESTION("suggestion", "建议");

    private final String value;

    private final String text;

    ComplaintTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取值列表
     *
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 获取标签列表
     *
     */
    public static List<String> getTexts() {
        return Arrays.stream(values()).map(item -> item.text).collect(Collectors.toList());
    }

}
