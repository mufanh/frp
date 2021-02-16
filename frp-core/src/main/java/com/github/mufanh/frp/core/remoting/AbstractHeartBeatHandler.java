package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.common.extension.ProxyContextFactory;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyServerConfig;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import io.netty.channel.ChannelDuplexHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public abstract class AbstractHeartBeatHandler extends ChannelDuplexHandler {

    protected final Protocol protocol;

    protected final ProxyContextFactory proxyContextFactory;

    public AbstractHeartBeatHandler(@NonNull final FrpContext frpContext, @NonNull final ProxyServerConfig proxyServerConfig) {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        if (extensionManager == null) {
            throw new IllegalStateException("代理执行容器未配置完成，无法正常使用");
        }
        this.protocol = extensionManager.protocol(proxyServerConfig.getAppName(), proxyServerConfig.getProtocol());
        if (this.protocol == null) {
            throw new IllegalArgumentException("代理服务配置错误，无法找到正确的服务协议");
        }
        this.proxyContextFactory = this.protocol.newProxyContextFactory();
    }

    protected ProxyContext createHeartBeat() {
        if (proxyContextFactory == null) {
            log.warn("协议{}未实现ProxyContext工厂方法，无法自动生成心跳请求", protocol.getClass().getName());
            return null;
        }
        return proxyContextFactory.createHeartBeat();
    }
}
