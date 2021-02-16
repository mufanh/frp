package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author xinquan.huangxq
 */
public class FrontendProxyServer extends NettyServerTemplate {

    public FrontendProxyServer(ProxyConfig proxyConfig, FrpContext frpContext) {
        super(prepareConfigFeature(proxyConfig), proxyConfig.getIp(), proxyConfig.getPort(), prepareChannelInitializer(proxyConfig, frpContext));
    }

    private static ChannelInitializer<SocketChannel> prepareChannelInitializer(ProxyConfig proxyConfig, FrpContext frpContext) {
        Protocol protocol = prepareProtocol(proxyConfig, frpContext);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast("idleHandler", new IdleStateHandler(
                        0, 0, prepareFrontendAccessIdleTime(proxyConfig)))
                        .addLast("encoder", protocol.newEncoder())
                        .addLast("decoder", protocol.newDecoder())
                        .addLast("context", new ExchangeProxyContextBuilder(frpContext, proxyConfig))
                        .addLast("connectionHandler", new FrontendConnectHandler(frpContext))
                        .addLast("proxyHandler", new FrontendProxyHandler(frpContext, proxyConfig));
            }
        };
    }

    private static Protocol prepareProtocol(ProxyConfig proxyConfig, FrpContext frpContext) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        Protocol protocol = extensionManager.protocol(proxyConfig.getProtocolType(), proxyConfig.getProtocolPluginId());
        if (protocol != null) {
            return protocol;
        }
        throw new IllegalArgumentException("未找到代理服务的编码、解码器");
    }

    private static int prepareFrontendAccessIdleTime(ProxyConfig proxyConfig) {
        return proxyConfig.getFrontendAccessIdleTime() == null
                ? SystemConfigs.FRONTEND_ACCESS_IDLE_TIME.intValue()
                : proxyConfig.getFrontendAccessIdleTime();
    }

    private static ConfigFeature prepareConfigFeature(ProxyConfig proxyConfig) {
        return new ConfigFeature()
                .addFeature(FEATURE_KEY_TCP_SO_RCVBUF, proxyConfig.getFrontendTcpSoRcvBuf())
                .addFeature(FEATURE_KEY_TCP_SO_SNDBUF, proxyConfig.getFrontendTcpSoSndBuf())
                .addFeature(FEATURE_KEY_NETTY_BUFFER_HIGH_WATERMARK, proxyConfig.getFrontendNettyBufferHighWatermark())
                .addFeature(FEATURE_KEY_NETTY_BUFFER_LOW_WATERMARK, proxyConfig.getFrontendNettyBufferLowWatermark());
    }
}
