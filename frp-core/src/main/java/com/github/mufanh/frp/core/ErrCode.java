package com.github.mufanh.frp.core;

/**
 * @author xinquan.huangxq
 */
public enum ErrCode {

    PROXY_BAD_REQUEST("PROXY_BAD_REQUEST", "报文异常"),

    PROXY_INNER_ERROR("PROXY_INNER_ERROR", "系统内部错误"),

    PROXY_SYSTEM_BUSY("PROXY_SYSTEM_BUSY", "系统繁忙"),

    PROXY_BACKEND_ERROR("PROXY_BACKEND_ERROR", "代理后端服务处理异常"),

    PROXY_NONE_SERVICE("PROXY_NONE_SERVICE", "未找到可用服务"),

    PROXY_ROUTE_ERROR("PROXY_ROUTE_ERROR", "代理服务执行异常"),

    PROXY_TIMEOUT("PROXY_TIMEOUT", "代理服务超时"),
    ;

    private final String code;

    private final String message;

    ErrCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
