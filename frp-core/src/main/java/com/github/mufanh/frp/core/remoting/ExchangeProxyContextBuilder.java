package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.common.extension.ProxyContextFactory;
import com.github.mufanh.frp.core.*;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public class ExchangeProxyContextBuilder extends MessageToMessageDecoder<ProxyContext> {

    private final FrpContext frpContext;

    private final ProxyConfig proxyConfig;

    public ExchangeProxyContextBuilder(final FrpContext frpContext, final ProxyConfig proxyConfig) {
        this.frpContext = frpContext;
        this.proxyConfig = proxyConfig;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ProxyContext context, List<Object> out) throws Exception {
        ExchangeProxyContext exchangeProxyContext = new DefaultExchangeProxyContext(context);

        exchangeProxyContext.setAppName(proxyConfig.getAppName());
        exchangeProxyContext.setProtocol(proxyConfig.getProtocol());

        exchangeProxyContext.setHeader(ExchangeProxyContext.HEADER_LOAD_BALANCE_TYPE,
                prepareLoadBalanceType());
        exchangeProxyContext.setExceptionHandler(prepareExceptionHandler(context, ctx.channel()));

        out.add(exchangeProxyContext);
    }

    private ExceptionHandler prepareExceptionHandler(ProxyContext context, Channel channel) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        if (extensionManager == null) {
            return createExceptionHandler(context, channel, null);
        }
        Protocol protocol = extensionManager.protocol(proxyConfig.getAppName(), proxyConfig.getProtocol());
        if (protocol == null) {
            return createExceptionHandler(context, channel, null);
        }
        return createExceptionHandler(context, channel, protocol.newProxyContextFactory());
    }

    private ExceptionHandler createExceptionHandler(ProxyContext context, Channel channel, ProxyContextFactory proxyContextFactory) {
        return new AbstractExceptionHandler() {
            @Override
            protected void response(ExchangeProxyContext context) {
                if (channel == null) {
                    return;
                }

                if (proxyContextFactory != null) {
                    ProxyContext responseContext = proxyContextFactory.createFailureProxyContext(
                            context, context.getCode(), context.getMessage());
                    if (responseContext != null) {
                        channel.writeAndFlush(responseContext);
                        return;
                    }
                }

                // 理论上来说不会走到这里，但是若协议没有定义，则用默认的
                context.setPayload(String.format("{\"msgId\":\"%s\"," +
                                "\"code\":\"%s\"," +
                                "\"message\":\"%s\"}",
                        context.getMsgId(), context.getCode(), context.getMessage()));
                channel.writeAndFlush(context);
            }
        };
    }

    private String prepareLoadBalanceType() {
        if (StringUtils.isBlank(proxyConfig.getLoadBalanceType())) {
            return null;
        }
        if (StringUtils.isNotBlank(proxyConfig.getLoadBalancePluginId())) {
            return proxyConfig.getLoadBalanceType() + "@" + proxyConfig.getLoadBalancePluginId();
        }
        return proxyConfig.getLoadBalanceType();
    }
}
