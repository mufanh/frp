package com.github.mufanh.frp.core;

/**
 * @author xinquan.huangxq
 */
public interface ExceptionHandler {

    void handleException(ExchangeProxyContext context, Throwable t);
}
