package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface ChannelHealthCheck extends LifeCycle {

    boolean check(ExchangeProxyContext context, Address address);

    interface Aware {

        void setChannelHealthCheck(ChannelHealthCheck channelHealthCheck);

        ChannelHealthCheck getChannelHealthCheck();
    }
}
