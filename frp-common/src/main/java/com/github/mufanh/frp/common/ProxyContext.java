package com.github.mufanh.frp.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理执行上下文
 *
 * @author xinquan.huangxq
 */
@Data
public class ProxyContext {

    public static final class HeaderKeys {
        /**
         * loadBalanceType@loadBalanceId，若没有@，则认为是null
         */
        public static final String LOAD_BALANCE = "__LOAD_BALANCE";
    }

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

    private Map<String, Object> headers;

    private Map<String, String> params;

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

    public ProxyContext setHeader(String headerKey, Object headerValue) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(headerKey, headerValue);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getHeader(String headerKey) {
        if (headerKey == null) {
            return null;
        }
        return (T) headers.get(headerKey);
    }

    public ProxyContext addParam(String paramKey, String paramValue) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(paramKey, paramValue);
        return this;
    }

    public String getParam(String paramKey) {
        return params == null ? null : params.get(paramKey);
    }
}
