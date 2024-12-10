package com.dgut.gq.www.common.common;

import lombok.Getter;

/**
 * 返回状态码和信息
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-2
 */
@Getter
public enum GlobalResponseCode {

    OPERATE_SUCCESS(200, "操作成功"),
    OPERATE_FAIL(999, "操作失败"),

    /**
     * 参数问题
     * 1000-1999
     */
    PARAM_INEFFECTIVE(1001, "参数无效"),
    PARAM_IS_NULL(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_LACK(1004, "参数缺失"),

    /**
     * 服务器或者系统问题
     * 2000-2999
     */
    SYSTEM_TIMEOUT(2001, "系统超时"),
    SYSTEM_REJECT(2002, "服务器拒绝请求"),
    SYSTEM_INTERIOR_ERROR(2003, "服务器内部错误"),
    SYSTEM_NOT_AVAILABLE(2004, "服务器不可用"),

    /**
     * 用户操作错误
     * 3000-3999
     */
    USER_NOT_LOGIN(3001, "用户未登录"),
    GQ_USER_ACCOUNT_OVERDUE(3002, "中央账户过期"),
    ACCOUNT_NOT_EXIST(3003, "账户不存在或者密码错误"),
    FAIL_GET_OPENID(3004, "获取openid失败"),
    USER_NOT_PERMISSIONS(3005, "账号无权限访问"),
    USER_NOT_RESOURCE(3006, "请求资源不存在");

    private Integer code;

    private String message;

    GlobalResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

