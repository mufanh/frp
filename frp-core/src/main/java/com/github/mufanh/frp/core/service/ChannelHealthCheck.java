package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.ProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface ChannelHealthCheck {

    boolean check(ProxyContext context, Address address);

    interface Aware {

        void setChannelHealthCheck(ChannelHealthCheck channelHealthCheck);

        ChannelHealthCheck getChannelHealthCheck();
    }
}
