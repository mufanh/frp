package com.github.mufanh.frp.common;

/**
 * @author xinquan.huangxq
 */
public interface ExceptionHandler {

    void handleException(ProxyContext context, Throwable t);
}
