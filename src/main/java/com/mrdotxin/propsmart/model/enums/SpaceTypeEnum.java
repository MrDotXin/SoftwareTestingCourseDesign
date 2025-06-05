package com.mrdotxin.propsmart.model.enums;


import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpaceTypeEnum {

    PUBLIC("公共开放", 0), // 100张图片 + 100MB容量
    PRIVATE("专业版", 1),
    AUTHORIZATION_ONLY("仅部分授权用户可以查看", 2);

    private final String text;

    private final Integer value;

    public static SpaceTypeEnum getEnumByValue(final Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        for (final SpaceTypeEnum env : values()) {
            if (env.getValue().equals(value)) {
                return env;
            }
        }

        return null;
    }

    public static Boolean isPublic(final Integer value) {
        return value.equals(PUBLIC.value);
    }

    public static Boolean isPrivate(final Integer value) {
        return value.equals(PRIVATE.value);
    }

    public static Boolean isAuthorizationOnly(final Integer value) {
        return value.equals(AUTHORIZATION_ONLY.value);
    }

}
