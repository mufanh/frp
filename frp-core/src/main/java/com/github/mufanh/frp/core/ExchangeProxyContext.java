package com.github.mufanh.frp.core;

import com.github.mufanh.frp.common.extension.ProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface ExchangeProxyContext extends ProxyContext {

    String HEADER_CHANNEL_ID = "__CHANNEL_ID";

    String HEADER_LOAD_BALANCE_TYPE = "__LOAD_BALANCE_TYPE";

    String getAppName();

    void setAppName(String appName);

    String getProtocol();

    void setProtocol(String protocol);

    /**
     * 代理执行过程中的异常
     */

    String setCode(String code);

    String getCode();

    void setMessage(String message);

    String getMessage();

    void setException(Throwable exception);

    Throwable getException();

    ExceptionHandler getExceptionHandler();

    void setExceptionHandler(ExceptionHandler exceptionHandler);
}
