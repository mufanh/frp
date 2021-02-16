package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.ProxyServerConfig;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import com.google.common.base.Preconditions;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author xinquan.huangxq
 */
public class BackendConnectionFactory extends AbstractConnectionFactory {

    public BackendConnectionFactory(ProxyServerConfig proxyServerConfig, FrpContext frpContext) {
        super(prepareConfigFeature(proxyServerConfig), prepareChannelInitializer(proxyServerConfig, frpContext));
    }

    private static ChannelInitializer<SocketChannel> prepareChannelInitializer(ProxyServerConfig proxyServerConfig, FrpContext frpContext) {
        Protocol protocol = prepareProtocol(proxyServerConfig, frpContext);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("idleHandler", new IdleStateHandler(
                                prepareBackendReadIdleTime(proxyServerConfig),
                                prepareBackendWriteIdleTime(proxyServerConfig),
                                prepareBackendAccessIdleTime(proxyServerConfig)))
                        .addLast("encoder", protocol.newEncoder())
                        .addLast("decoder", protocol.newDecoder())
                        .addLast("connectionHandler", new BackendConnectHandler(frpContext, proxyServerConfig))
                        .addLast("proxyHandler", new BackendProxyHandler(frpContext));
            }
        };
    }

    private static Protocol prepareProtocol(ProxyServerConfig proxyServerConfig, FrpContext frpContext) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        Preconditions.checkNotNull(frpContext.getExtensionManager());

        Protocol protocol = extensionManager.protocol(proxyServerConfig.getProtocolType(), proxyServerConfig.getProtocolPluginId());
        if (protocol != null) {
            return protocol;
        }
        throw new IllegalArgumentException("未找到代理服务的编码、解码器");
    }

    private static ConfigFeature prepareConfigFeature(ProxyServerConfig proxyServerConfig) {
        return new ConfigFeature()
                .addFeature(FEATURE_KEY_NETTY_BUFFER_HIGH_WATERMARK, proxyServerConfig.getBackendNettyBufferHighWatermark())
                .addFeature(FEATURE_KEY_NETTY_BUFFER_LOW_WATERMARK, proxyServerConfig.getBackendNettyBufferLowWatermark());
    }

    private static int prepareBackendReadIdleTime(ProxyServerConfig proxyServerConfig) {
        return proxyServerConfig.getBackendReadIdleTime() == null
                ? SystemConfigs.BACKEND_READ_IDLE_TIME.intValue()
                : proxyServerConfig.getBackendReadIdleTime();
    }

    private static int prepareBackendWriteIdleTime(ProxyServerConfig proxyServerConfig) {
        return proxyServerConfig.getBackendWriteIdleTime() == null
                ? SystemConfigs.BACKEND_WRITE_IDLE_TIME.intValue()
                : proxyServerConfig.getBackendWriteIdleTime();
    }

    private static int prepareBackendAccessIdleTime(ProxyServerConfig proxyServerConfig) {
        return proxyServerConfig.getBackendAccessIdleTime() == null
                ? SystemConfigs.BACKEND_ACCESS_IDLE_TIME.intValue()
                : proxyServerConfig.getBackendAccessIdleTime();
    }
}
