package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Codec;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import com.sun.istack.internal.NotNull;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author xinquan.huangxq
 */
public class FrontendProxyServer extends ServerTemplate {

    public FrontendProxyServer(ProxyConfig proxyConfig, FrpContext frpContext) {
        super(prepareConfigFeature(proxyConfig), proxyConfig.getIp(), proxyConfig.getPort(), prepareChannelInitializer(proxyConfig, frpContext));
    }

    private static ChannelInitializer<SocketChannel> prepareChannelInitializer(ProxyConfig proxyConfig, FrpContext frpContext) {
        IdleStateHandler idleStateHandler = new IdleStateHandler(
                0, 0, prepareFrontendAccessIdleTime(proxyConfig));
        Codec codec = prepareCodec(proxyConfig, frpContext);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast("idleHandler", idleStateHandler)
                        .addLast("encoder", codec.newEncoder())
                        .addLast("decoder", codec.newDecoder())
                        .addLast("connectionHandler", new FrontendConnectHandler(frpContext))
                        .addLast("proxyHandler", new FrontendProxyHandler(frpContext));

                createConnection(channel);
            }

            private void createConnection(Channel channel) {
                new Connection(channel);
            }
        };
    }

    @NotNull
    private static Codec prepareCodec(ProxyConfig proxyConfig, FrpContext frpContext) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        Codec codec = extensionManager.codec(proxyConfig.getCodecType(), proxyConfig.getCodecPluginId());
        if (codec != null) {
            return codec;
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
                .addFeature(FeatureKeys.TCP_SO_RCVBUF, proxyConfig.getFrontendTcpSoRcvBuf())
                .addFeature(FeatureKeys.TCP_SO_SNDBUF, proxyConfig.getFrontendTcpSoSndBuf())
                .addFeature(FeatureKeys.NETTY_BUFFER_HIGH_WATERMARK, proxyConfig.getFrontendNettyBufferHighWatermark())
                .addFeature(FeatureKeys.NETTY_BUFFER_LOW_WATERMARK, proxyConfig.getFrontendNettyBufferLowWatermark());
    }
}
