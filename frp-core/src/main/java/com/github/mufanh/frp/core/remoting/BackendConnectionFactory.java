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
public class BackendConnectionFactory extends AbstractConnectionFactory {

    public BackendConnectionFactory(ProxyConfig proxyConfig, FrpContext frpContext) {
        super(prepareConfigFeature(proxyConfig), prepareChannelInitializer(proxyConfig, frpContext));
    }

    private static ChannelInitializer<SocketChannel> prepareChannelInitializer(ProxyConfig proxyConfig, FrpContext frpContext) {
        Protocol protocol = prepareProtocol(proxyConfig, frpContext);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("idleHandler", new IdleStateHandler(
                                prepareBackendReadIdleTime(proxyConfig),
                                prepareBackendWriteIdleTime(proxyConfig),
                                prepareBackendAccessIdleTime(proxyConfig)))
                        .addLast("encoder", protocol.newEncoder())
                        .addLast("decoder", protocol.newDecoder())
                        .addLast("connectionHandler", new BackendConnectHandler(frpContext, proxyConfig))
                        .addLast("proxyHandler", new BackendProxyHandler(frpContext));
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

    private static ConfigFeature prepareConfigFeature(ProxyConfig proxyConfig) {
        return new ConfigFeature()
                .addFeature(FEATURE_KEY_NETTY_BUFFER_HIGH_WATERMARK, proxyConfig.getBackendNettyBufferHighWatermark())
                .addFeature(FEATURE_KEY_NETTY_BUFFER_LOW_WATERMARK, proxyConfig.getBackendNettyBufferLowWatermark());
    }

    private static int prepareBackendReadIdleTime(ProxyConfig proxyConfig) {
        return proxyConfig.getBackendReadIdleTime() == null
                ? SystemConfigs.BACKEND_READ_IDLE_TIME.intValue()
                : proxyConfig.getBackendReadIdleTime();
    }

    private static int prepareBackendWriteIdleTime(ProxyConfig proxyConfig) {
        return proxyConfig.getBackendWriteIdleTime() == null
                ? SystemConfigs.BACKEND_WRITE_IDLE_TIME.intValue()
                : proxyConfig.getBackendWriteIdleTime();
    }

    private static int prepareBackendAccessIdleTime(ProxyConfig proxyConfig) {
        return proxyConfig.getBackendAccessIdleTime() == null
                ? SystemConfigs.BACKEND_ACCESS_IDLE_TIME.intValue()
                : proxyConfig.getBackendAccessIdleTime();
    }
}
