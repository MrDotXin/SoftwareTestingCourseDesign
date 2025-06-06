package com.mrdotxin.propsmart.common;

/**
 * 返回工具类
 *

 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 返回的数据
     * @param <T> 返回数据的泛型类型
     * @return 封装了数据的成功响应
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 封装了错误信息的失败响应
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code 错误码
     * @param message 错误信息
     * @return 封装了错误信息的失败响应
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码枚举
     * @param message 错误信息
     * @return 封装了错误信息的失败响应
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
