package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.Cluster;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.core.LifeCycle;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public interface ChannelChooseService extends LifeCycle {

    ChooseResult choose(ProxyContext context, Cluster cluster);

    ChooseResult choose(ProxyContext context, List<Address> addresses);

    interface Aware {

        void setChannelChooseService(ChannelChooseService channelChooseService);

        ChannelChooseService getChannelChooseService();
    }
}
