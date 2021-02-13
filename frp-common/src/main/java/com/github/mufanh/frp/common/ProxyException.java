package com.github.mufanh.frp.common;

/**
 * @author xinquan.huangxq
 */
public class ProxyException extends Exception {

    private final ErrCode errCode;

    public ProxyException(ErrCode errCode) {
        super(errCode.message());
        this.errCode = errCode;
    }

    public ProxyException(ErrCode errCode, String detailMessage) {
        super(detailMessage);
        this.errCode = errCode;
    }
}
