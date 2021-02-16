package com.github.mufanh.frp.common;

import com.github.mufanh.frp.common.extension.ProxyContext;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 默认实现，也可以自行扩展，继承DefaultProxyContext，甚至直接实现接口
 *
 * @author xinquan.huangxq
 */
public class DefaultProxyContext implements ProxyContext {

    private final boolean heartBeat;

    private String msgId;

    private Map<String, Object> headers;

    private Map<String, String> params;

    private String payload;

    private DefaultProxyContext(boolean heartBeat) {
        this.heartBeat = heartBeat;
    }

    public static ProxyContext build() {
        return new DefaultProxyContext(false);
    }

    public static ProxyContext buildHeartBeat() {
        return new DefaultProxyContext(true);
    }

    @Override
    public boolean isHeartBeat() {
        return this.heartBeat;
    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    @Override
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHeader(String name) {
        if (headers == null) {
            return null;
        }
        return (T) headers.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHeader(String name, T defaultValue) {
        if (headers == null) {
            return defaultValue;
        }
        return headers.containsKey(name)
                ? (T) headers.get(name)
                : defaultValue;
    }

    @Override
    public void setHeader(String name, Object value) {
        if (headers == null) {
            headers = Maps.newHashMapWithExpectedSize(4);
        }
        headers.put(name, value);
    }

    @Override
    public Object removeHeader(String name) {
        if (headers == null) {
            return null;
        }
        return headers.remove(name);
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    @Override
    public String getParam(String name) {
        if (params == null) {
            return null;
        }
        return params.get(name);
    }

    @Override
    public void setParam(String name, String value) {
        if (params == null) {
            params = Maps.newHashMapWithExpectedSize(4);
        }
        params.put(name, value);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
