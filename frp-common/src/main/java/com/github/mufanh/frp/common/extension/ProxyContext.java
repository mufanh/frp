package com.github.mufanh.frp.common.extension;

import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public interface ProxyContext {

    String HEADER_IP = "__IP";

    /**
     * 是否是心跳请求
     */
    boolean isHeartBeat();

    /**
     * 请求和响应的msgId
     */

    String getMsgId();

    void setMsgId(String msgId);

    /**
     * header
     */

    <T> T getHeader(String name);

    <T> T getHeader(String name, T defaultValue);

    void setHeader(String name, Object value);

    Object removeHeader(String name);

    Map<String, Object> getHeaders();

    void setHeaders(Map<String, Object> headers);

    /**
     * 分流特征
     */

    String getParam(String name);

    void setParam(String name, String value);

    Map<String, String> getParams();

    void setParams(Map<String, String> params);

    /**
     * 原始报文（请求原始报文、响应原始报文）
     */

    String getPayload();

    void setPayload(String payload);
}
