package com.github.mufanh.frp.common;

/**
 * @author xinquan.huangxq
 */
public interface IExceptionHandler {

    void handleException(ProxyContext context, Throwable t);
}
