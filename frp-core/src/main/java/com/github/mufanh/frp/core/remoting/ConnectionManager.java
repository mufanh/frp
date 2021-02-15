package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.Address;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.Set;

/**
 * @author xinquan.huangxq
 */
public interface ConnectionManager {

    /**
     * 前端连接
     */

    void addFrontendChannel(Channel channel);

    Channel getFrontendChannel(ChannelId channelId);

    Channel removeFrontendChannel(ChannelId channelId);

    Channel getFrontendChannelIfActive(ChannelId channelId);

    /**
     * 后端连接
     */

    Set<Address> backendChannelAddresses();

    void addBackendChannel(Address address, Channel channel);

    void removeBackendChannel(Address address);

    void removeBackendChannelWithoutClose(Address address);

    Address removeBackendChannel(Channel channel);

    Channel getBackendChannel(Address address);

    Channel getBackendChannelIfActive(Address address);

    /**
     * 重连列表
     */

    Set<Address> tryConnectBackendAddresses();

    void addTryConnectBackendAddress(Address address);

    void removeTryConnectBackendAddress(Address address);

    int getBackendAddressTryConnectTimes(Address address);

    /**
     * 延迟重连列表
     */

    Set<Address> delayTryConnectBackendAddresses();

    void addDelayTryConnectBackendAddress(Address address);

    void removeDelayTryConnectBackendAddress(Address address);

    interface Aware {

        void setConnectionManager(ConnectionManager connectionManager);

        ConnectionManager getConnectionManager();
    }
}
