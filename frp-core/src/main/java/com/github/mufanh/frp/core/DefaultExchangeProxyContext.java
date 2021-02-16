package com.github.mufanh.frp.core;

import com.github.mufanh.frp.common.extension.ProxyContext;

import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public class DefaultExchangeProxyContext implements ExchangeProxyContext {

    /**
     * 经过服务解码器后获取的context（该context内容由接入服务业务方定义）
     */
    private final ProxyContext proxyContext;

    private String appName;

    private String protocol;

    private Throwable exception;

    private String code;

    private String message;

    private ExceptionHandler exceptionHandler;

    public DefaultExchangeProxyContext(final ProxyContext proxyContext) {
        this.proxyContext = proxyContext;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String setCode(String code) {
        return code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean isHeartBeat() {
        return proxyContext.isHeartBeat();
    }

    @Override
    public String getMsgId() {
        return proxyContext.getMsgId();
    }

    @Override
    public void setMsgId(String msgId) {
        proxyContext.setMsgId(msgId);
    }

    @Override
    public <T> T getHeader(String name) {
        return proxyContext.getHeader(name);
    }

    @Override
    public <T> T getHeader(String name, T defaultValue) {
        return proxyContext.getHeader(name, defaultValue);
    }

    @Override
    public void setHeader(String name, Object value) {
        proxyContext.setHeader(name, value);
    }

    @Override
    public Object removeHeader(String name) {
        return proxyContext.removeHeader(name);
    }

    @Override
    public Map<String, Object> getHeaders() {
        return proxyContext.getHeaders();
    }

    @Override
    public void setHeaders(Map<String, Object> headers) {
        proxyContext.setHeaders(headers);
    }

    @Override
    public String getParam(String name) {
        return proxyContext.getParam(name);
    }

    @Override
    public void setParam(String name, String value) {
        proxyContext.setParam(name, value);
    }

    @Override
    public Map<String, String> getParams() {
        return proxyContext.getParams();
    }

    @Override
    public void setParams(Map<String, String> params) {
        proxyContext.setParams(params);
    }

    @Override
    public String getPayload() {
        return proxyContext.getPayload();
    }

    @Override
    public void setPayload(String originPayload) {
        proxyContext.setPayload(originPayload);
    }
}
