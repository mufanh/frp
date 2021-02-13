package com.github.mufanh.frp.common;

/**
 * @author xinquan.huangxq
 */
public enum ErrCode {

    PROXY_SYSTEM_BUSY("PROXY_SYSTEM_BUSY", "系统繁忙"),
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
