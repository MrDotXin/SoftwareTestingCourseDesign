package com.mrdotxin.propsmart.utils;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

public class FormatUtils {

    // 中文姓名正则（2-10个中文字符，可包含少数民族姓名中的点）
    private static final String CHINESE_NAME_REGEX = "^[\\u4e00-\\u9fa5·]{2,10}$";
    // 简单手机号正则（1开头，11位数字）
    private static final String SIMPLE_PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 验证中文姓名合法性
     *
     * @param name 待验证的姓名
     * @return 是否合法
     */
    public static boolean isValidChineseName(String name) {
        if (StrUtil.isBlank(name)) {
            return false;
        }
        return ReUtil.isMatch(CHINESE_NAME_REGEX, name);
    }

    /**
     * 验证身份证号码合法性（使用Hutool的IdcardUtil）
     *
     * @param idCard 待验证的身份证号
     * @return 是否合法
     */
    public static boolean isValidIdCard(String idCard) {
        if (StrUtil.isBlank(idCard)) {
            return false;
        }
        return IdcardUtil.isValidCard(idCard);
    }

    /**
     * 验证手机号格式（简单验证）
     *
     * @param phone 待验证的手机号
     * @return 是否合法
     */
    public static boolean isValidPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }
        return ReUtil.isMatch(SIMPLE_PHONE_REGEX, phone);
    }

    /**
     * 验证身份证和姓名是否匹配（简单版，仅验证格式）
     *
     * @param name   姓名
     * @param idCard 身份证号
     * @return 是否都合法
     */
    public static boolean isValidNameAndIdCard(String name, String idCard) {
        return isValidChineseName(name) && isValidIdCard(idCard);
    }

    /**
     * 从身份证号提取出生日期（yyyy-MM-dd格式）
     *
     * @param idCard 身份证号
     * @return 出生日期字符串，验证失败返回null
     */
    public static String getBirthFromIdCard(String idCard) {
        if (!isValidIdCard(idCard)) {
            return null;
        }
        return IdcardUtil.getBirthByIdCard(idCard);
    }

    /**
     * 从身份证号提取性别（1:男，0:女）
     *
     * @param idCard 身份证号
     * @return 性别代码，验证失败返回-1
     */
    public static int getGenderFromIdCard(String idCard) {
        if (!isValidIdCard(idCard)) {
            return -1;
        }
        return IdcardUtil.getGenderByIdCard(idCard);
    }
}