package com.dgut.gq.www.common.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

/**
 * 返回给前端的统一参数
 * 该类继承Map防止传递空值
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-2
 */
@Data
@NoArgsConstructor
public class SystemJsonResponse extends LinkedHashMap<String, Object> {
    /**
     * 状态码
     */
    private static final String RESULT_CODE = "code";

    /**
     * 响应消息
     */
    private static final String RESULT_MESSAGE = "message";

    /**
     * 响应数据
     */
    private static final String RESULT_DATA = "data";

    public SystemJsonResponse(int code, String message, Object data) {
        this.put(RESULT_CODE, code);
        if (message != null) {
            this.put(RESULT_MESSAGE, message);
        }
        if (data != null) {
            this.put(RESULT_DATA, data);
        }
    }

    public SystemJsonResponse(int code, String message) {
        this.put(RESULT_CODE, code);
        if (message != null) {
            this.put(RESULT_MESSAGE, message);
        }
    }

    /**
     * 无数据成功操作返回
     */
    public static SystemJsonResponse success() {
        return new SystemJsonResponse(GlobalResponseCode.OPERATE_SUCCESS.getCode(), GlobalResponseCode.OPERATE_SUCCESS.getMessage());
    }

    /**
     * 带数据的成功操作返回
     */
    public static SystemJsonResponse success(Object data) {
        return new SystemJsonResponse(GlobalResponseCode.OPERATE_SUCCESS.getCode(), GlobalResponseCode.OPERATE_SUCCESS.getMessage(), data);
    }

    /**
     * 自定义返回码和信息
     */
    public static SystemJsonResponse success(int code, String message) {
        return new SystemJsonResponse(code, message);
    }

    /**
     * 无数据失败操作返回
     */
    public static SystemJsonResponse fail() {
        return new SystemJsonResponse(GlobalResponseCode.OPERATE_FAIL.getCode(), GlobalResponseCode.OPERATE_FAIL.getMessage());
    }

    /**
     * 带数据失败操作返回
     */
    public static SystemJsonResponse fail(Object data) {
        return new SystemJsonResponse(GlobalResponseCode.OPERATE_FAIL.getCode(), GlobalResponseCode.OPERATE_FAIL.getMessage(), data);
    }

    /**
     * 自定义返回码和信息
     */
    public static SystemJsonResponse fail(int code, String message) {
        return new SystemJsonResponse(code, message);
    }


}
