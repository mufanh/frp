package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import io.netty.channel.ChannelDuplexHandler;

/**
 * @author xinquan.huangxq
 */
public abstract class AbstractProxyHandler extends ChannelDuplexHandler {

    protected final FrpContext frpContext;

    protected final ProxyConfig proxyConfig;

    public AbstractProxyHandler(final FrpContext frpContext, final ProxyConfig proxyConfig) {
        this.frpContext = frpContext;
        this.proxyConfig = proxyConfig;
    }

    protected ProxyContext createHeartBeat() {
        ExtensionManager extensionManager = frpContext.getExtensionManager();
        if (extensionManager == null) {
            return null;
        }
        Protocol protocol = extensionManager.protocol(
                proxyConfig.getProtocolType(), proxyConfig.getProtocolPluginId());
        if (protocol == null) {
            return null;
        }
        return protocol.newProxyContextFactory().createHeartBeat();
    }
}
