package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface ChannelHealthCheck extends LifeCycle {

    boolean check(ProxyContext context, Address address);

    interface Aware {

        void setChannelHealthCheck(ChannelHealthCheck channelHealthCheck);

        ChannelHealthCheck getChannelHealthCheck();
    }
}
