package com.mrdotxin.propsmart.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpaceLevelEnum {

    COMMON("普通版", 0, 100L, 100 * 1024 * 1024L), // 100张图片 + 100MB容量
    PROFESSIONAL("专业版", 1, 1000L, 1024 * 1024 * 1024L), // 1000张图片 + 1G容量
    FLAGSHIP("旗舰版", 2, 100000L, 10 * 1024 * 1024 * 1024L); // 10w张图片 + 10G容量

    private final String text;

    private final Integer value;

    private final Long maxSize;

    private final Long maxCount;

    public static SpaceLevelEnum getEnumByValue(final Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        for (final SpaceLevelEnum spaceLevelEnum : values()) {
            if (spaceLevelEnum.getValue().equals(value)) {
                return spaceLevelEnum;
            }
        }

        return null;
    }
}
