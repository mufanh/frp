package com.github.mufanh.frp.common;

import lombok.Data;

/**
 * 代理执行上下文
 *
 * @author xinquan.huangxq
 */
@Data
public class ProxyContext {

    public static final ProxyContext HEARTBEAT = new ProxyContext(true);

    private final boolean heartBeat;

    /**
     * 消息msgId
     */
    private String msgId;

    /**
     * 记录原始报文
     */
    private String payload;

    private String appName;

    private String protocol;

    /**
     * 处理异常
     */
    private Throwable exception;

    private ErrCode errCode;

    private String detailErrorMsg;

    /**
     * 异常处理
     */
    private ExceptionHandler exceptionHandler;

    private ProxyContext(boolean heartBeat) {
        this.heartBeat = heartBeat;
    }

    public ProxyContext() {
        this(false);
    }
}
