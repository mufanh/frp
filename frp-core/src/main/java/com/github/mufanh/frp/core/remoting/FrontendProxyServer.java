package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.ProxyServerConfig;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author xinquan.huangxq
 */
public class FrontendProxyServer extends NettyServerTemplate {

    public FrontendProxyServer(ProxyServerConfig proxyServerConfig, FrpContext frpContext) {
        super(prepareConfigFeature(proxyServerConfig), proxyServerConfig.getIp(), proxyServerConfig.getPort(), prepareChannelInitializer(proxyServerConfig, frpContext));
    }

    private static ChannelInitializer<SocketChannel> prepareChannelInitializer(ProxyServerConfig proxyServerConfig, FrpContext frpContext) {
        Protocol protocol = prepareProtocol(proxyServerConfig, frpContext);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast("idleHandler", new IdleStateHandler(
                        0, 0, prepareFrontendAccessIdleTime(proxyServerConfig)))
                        .addLast("encoder", protocol.newEncoder())
                        .addLast("decoder", protocol.newDecoder())
                        .addLast("context", new ExchangeProxyContextBuilder(frpContext, proxyServerConfig))
                        .addLast("connectionHandler", new FrontendConnectHandler(frpContext))
                        .addLast("proxyHandler", new FrontendHeartBeatHandler(frpContext, proxyServerConfig));
            }
        };
    }

    private static Protocol prepareProtocol(ProxyServerConfig proxyServerConfig, FrpContext frpContext) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        Protocol protocol = extensionManager.protocol(proxyServerConfig.getProtocolType(), proxyServerConfig.getProtocolPluginId());
        if (protocol != null) {
            return protocol;
        }
        throw new IllegalArgumentException("未找到代理服务的编码、解码器");
    }

    private static int prepareFrontendAccessIdleTime(ProxyServerConfig proxyServerConfig) {
        return proxyServerConfig.getFrontendAccessIdleTime() == null
                ? SystemConfigs.FRONTEND_ACCESS_IDLE_TIME.intValue()
                : proxyServerConfig.getFrontendAccessIdleTime();
    }

    private static ConfigFeature prepareConfigFeature(ProxyServerConfig proxyServerConfig) {
        return new ConfigFeature()
                .addFeature(FEATURE_KEY_TCP_SO_RCVBUF, proxyServerConfig.getFrontendTcpSoRcvBuf())
                .addFeature(FEATURE_KEY_TCP_SO_SNDBUF, proxyServerConfig.getFrontendTcpSoSndBuf())
                .addFeature(FEATURE_KEY_NETTY_BUFFER_HIGH_WATERMARK, proxyServerConfig.getFrontendNettyBufferHighWatermark())
                .addFeature(FEATURE_KEY_NETTY_BUFFER_LOW_WATERMARK, proxyServerConfig.getFrontendNettyBufferLowWatermark());
    }
}
