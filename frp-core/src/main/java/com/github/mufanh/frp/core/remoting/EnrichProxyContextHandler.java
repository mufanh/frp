package com.github.mufanh.frp.core.remoting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mufanh.frp.common.AbstractExceptionHandler;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public class EnrichProxyContextHandler extends MessageToMessageDecoder<ProxyContext> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final ProxyConfig proxyConfig;

    public EnrichProxyContextHandler(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ProxyContext context, List<Object> out) throws Exception {
        context.setAppName(proxyConfig.getAppName());
        context.setProtocol(proxyConfig.getProtocol());

        if (StringUtils.isNotBlank(proxyConfig.getLoadBalanceType())
                && StringUtils.isNotBlank(proxyConfig.getLoadBalancePluginId())) {
            context.setHeader(ProxyContext.HeaderKeys.LOAD_BALANCE,
                    proxyConfig.getLoadBalanceType() + "@" + proxyConfig.getLoadBalancePluginId());
        } else if (StringUtils.isNotBlank(proxyConfig.getLoadBalanceType())) {
            context.setHeader(ProxyContext.HeaderKeys.LOAD_BALANCE, proxyConfig.getLoadBalanceType());
        }

        context.setExceptionHandler(new AbstractExceptionHandler() {
            @Override
            protected void response(ProxyContext context) {
                if (ctx.channel() == null) {
                    return;
                }

                Map<String, String> result = Maps.newHashMap();
                if (context.getErrCode() != null) {
                    result.put("code", context.getErrCode().code());
                    result.put("message", StringUtils.isBlank(context.getDetailErrorMsg())
                            ? context.getErrCode().message() : context.getDetailErrorMsg());
                }
                try {
                    context.setPayload(mapper.writeValueAsString(result));
                } catch (JsonProcessingException ignored) {
                }

                if (ctx.channel().isActive()) {
                    ctx.channel().writeAndFlush(context);
                }
            }
        });
        out.add(context);
    }
}
