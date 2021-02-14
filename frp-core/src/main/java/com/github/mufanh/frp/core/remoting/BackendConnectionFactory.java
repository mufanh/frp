package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Codec;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public class BackendConnectionFactory extends AbstractConnectionFactory {

    public BackendConnectionFactory(ProxyConfig proxyConfig, FrpContext frpContext) {
        super(prepareConfigFeature(proxyConfig), prepareChannelInitializer(proxyConfig, frpContext));
    }

    private static ChannelInitializer<SocketChannel> prepareChannelInitializer(ProxyConfig proxyConfig, FrpContext frpContext) {
        IdleStateHandler idleStateHandler = new IdleStateHandler(
                prepareBackendReadIdleTime(proxyConfig),
                prepareBackendWriteIdleTime(proxyConfig),
                prepareBackendAccessIdleTime(proxyConfig));
        Codec codec = prepareCodec(proxyConfig, frpContext);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("idleHandler", idleStateHandler)
                        .addLast("encoder", codec.newEncoder())
                        .addLast("decoder", codec.newDecoder())
                        .addLast("connectionHandler", new BackendConnectHandler())
                        .addLast("proxyHandler", new BackendProxyHandler(frpContext));
            }
        };
    }

    @NotNull
    private static Codec prepareCodec(ProxyConfig proxyConfig, FrpContext frpContext) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        List<Codec> codecs = extensionManager.getExtensions(Codec.class, proxyConfig.getCodecPluginId());
        for (Codec codec : codecs) {
            if (StringUtils.equals(codec.getClass().getName(), proxyConfig.getCodecType())) {
                return codec;
            }
        }
        throw new IllegalArgumentException("未找到代理服务的编码、解码器");
    }

    private static ConfigFeature prepareConfigFeature(ProxyConfig proxyConfig) {
        return new ConfigFeature()
                .addFeature(FeatureKeys.NETTY_BUFFER_HIGH_WATERMARK, proxyConfig.getBackendNettyBufferHighWatermark())
                .addFeature(FeatureKeys.NETTY_BUFFER_LOW_WATERMARK, proxyConfig.getBackendNettyBufferLowWatermark());
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
