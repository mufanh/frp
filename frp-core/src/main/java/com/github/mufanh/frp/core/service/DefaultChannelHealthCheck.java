package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.remoting.ConnectionManager;
import io.netty.channel.Channel;

/**
 * @author xinquan.huangxq
 */
public class DefaultChannelHealthCheck extends AbstractLifeCycle implements ChannelHealthCheck {

    private final FrpContext frpContext;

    private ConnectionManager connectionManager;

    public DefaultChannelHealthCheck(FrpContext frpContext) {
        this.frpContext = frpContext;
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        connectionManager = frpContext.getConnectionManager();
    }

    @Override
    public boolean check(ExchangeProxyContext context, Address address) {
        Channel channel = connectionManager.getBackendChannelIfActive(address);
        return channel != null;
    }
}
