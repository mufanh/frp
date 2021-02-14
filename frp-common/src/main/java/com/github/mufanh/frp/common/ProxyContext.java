package com.github.mufanh.frp.common;

import lombok.Data;

/**
 * 代理执行上下文
 *
 * @author xinquan.huangxq
 */
@Data
public class ProxyContext {
    
    public static final ProxyContext HEARTBEAT = new ProxyContext();

    /**
     * 消息msgId
     */
    private String msgId;

    /**
     * 记录原始报文
     */
    private String payload;

    /**
     * 处理异常
     */
    private Throwable exception;

    /**
     * 异常处理
     */
    private IExceptionHandler exceptionHandler;
}
