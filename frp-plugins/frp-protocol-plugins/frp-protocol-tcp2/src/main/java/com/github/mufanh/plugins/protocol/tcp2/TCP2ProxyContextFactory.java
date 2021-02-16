package com.github.mufanh.plugins.protocol.tcp2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.mufanh.frp.common.DefaultProxyContext;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.common.extension.ProxyContextFactory;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public class TCP2ProxyContextFactory implements ProxyContextFactory {

    @Override
    public ProxyContext createHeartBeat() {
        return DefaultProxyContext.buildHeartBeat();
    }

    @Override
    public ProxyContext createProxyContext() {
        return DefaultProxyContext.build();
    }

    @Override
    public ProxyContext createFailureProxyContext(ProxyContext requestProxyContext, String code, String message) {
        Map<String, Object> resultJson = Maps.newHashMap();
        resultJson.put("msgId", requestProxyContext.getMsgId());
        resultJson.put("code", code);
        resultJson.put("message", message);

        ProxyContext result = createProxyContext();
        result.setMsgId(requestProxyContext.getMsgId());
        try {
            result.setPayload(JSONUtil.map2Json(resultJson));
        } catch (JsonProcessingException e) {
            // 理论上来说不会出现该异常
            result.setPayload(String.format("{\"msgId\":\"%s\"," +
                            "\"code\":\"%s\"," +
                            "\"message\":\"%s\"}",
                    requestProxyContext.getMsgId(), code, message));
        }
        return result;
    }
}
