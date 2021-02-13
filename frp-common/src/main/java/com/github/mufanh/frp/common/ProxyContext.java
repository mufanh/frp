package com.github.mufanh.frp.common;

import lombok.Data;

/**
 * 代理执行上下文
 *
 * @author xinquan.huangxq
 */
@Data
public class ProxyContext {

    /**
     * 处理异常
     */
    private Throwable exception;

    /**
     * 异常处理
     */
    private IExceptionHandler exceptionHandler;
}
