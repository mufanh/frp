package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.common.extension.ProxyContextFactory;
import com.github.mufanh.frp.core.*;
import com.github.mufanh.frp.core.config.ProxyServerConfig;
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

    private final ProxyServerConfig proxyServerConfig;

    private final ExtensionManager extensionManager;

    public ExchangeProxyContextBuilder(final FrpContext frpContext, final ProxyServerConfig proxyServerConfig) {
        this.proxyServerConfig = proxyServerConfig;
        this.extensionManager = frpContext.getExtensionManager();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ProxyContext context, List<Object> out) throws Exception {
        ExchangeProxyContext exchangeProxyContext = new DefaultExchangeProxyContext(context);

        exchangeProxyContext.setAppName(proxyServerConfig.getAppName());
        exchangeProxyContext.setProtocol(proxyServerConfig.getProtocol());

        exchangeProxyContext.setHeader(ExchangeProxyContext.HEADER_LOAD_BALANCE_TYPE,
                prepareLoadBalanceType());
        exchangeProxyContext.setExceptionHandler(prepareExceptionHandler(ctx.channel()));

        out.add(exchangeProxyContext);
    }

    private ExceptionHandler prepareExceptionHandler(Channel channel) {
        Protocol protocol = extensionManager.protocol(proxyServerConfig.getProtocolType(), proxyServerConfig.getProtocolPluginId());
        if (protocol == null) {
            return createExceptionHandler(channel, null);
        }
        return createExceptionHandler(channel, protocol.newProxyContextFactory());
    }

    private ExceptionHandler createExceptionHandler(Channel channel, ProxyContextFactory proxyContextFactory) {
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
                        if (channel.isActive()) {
                            channel.writeAndFlush(responseContext);
                        }
                        return;
                    }
                }

                // 理论上来说不会走到这里，但是若协议没有定义，或者错误，则用默认的
                context.setPayload(String.format("{\"msgId\":\"%s\"," +
                                "\"code\":\"%s\"," +
                                "\"message\":\"%s\"}",
                        context.getMsgId(), context.getCode(), context.getMessage()));
                channel.writeAndFlush(context);
            }
        };
    }

    private String prepareLoadBalanceType() {
        if (StringUtils.isBlank(proxyServerConfig.getLoadBalanceType())) {
            return null;
        }
        if (StringUtils.isNotBlank(proxyServerConfig.getLoadBalancePluginId())) {
            return proxyServerConfig.getLoadBalanceType() + "@" + proxyServerConfig.getLoadBalancePluginId();
        }
        return proxyServerConfig.getLoadBalanceType();
    }
}
