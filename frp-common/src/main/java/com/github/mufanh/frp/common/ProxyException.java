package com.github.mufanh.frp.common;

import lombok.Getter;

/**
 * @author xinquan.huangxq
 */
public class ProxyException extends Exception {

    @Getter
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
