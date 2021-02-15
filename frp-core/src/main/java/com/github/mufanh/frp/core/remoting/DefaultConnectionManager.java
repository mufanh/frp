package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.Address;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
public class DefaultConnectionManager implements ConnectionManager {

    private final Map<ChannelId, Channel> frontendChannelMap = Maps.newConcurrentMap();

    private final Map<Address, Channel> backendChannelMap = Maps.newConcurrentMap();

    @Override
    public void addFrontendChannel(Channel channel) {
        frontendChannelMap.putIfAbsent(channel.id(), channel);
    }

    @Override
    public Channel getFrontendChannel(ChannelId channelId) {
        return frontendChannelMap.get(channelId);
    }

    @Override
    public Channel removeFrontendChannel(ChannelId channelId) {
        return frontendChannelMap.remove(channelId);
    }

    @Override
    public Channel getFrontendChannelIfActive(ChannelId channelId) {
        Channel channel = getFrontendChannel(channelId);
        if (channel != null && channel.isActive()) {
            return channel;
        }
        removeFrontendChannel(channelId);
        return null;
    }

    @Override
    public Set<Address> backendChannelAddresses() {
        return backendChannelMap.keySet();
    }

    @Override
    public void addBackendChannel(Address address, Channel channel) {
        backendChannelMap.putIfAbsent(address, channel);
    }

    @Override
    public void removeBackendChannel(Address address) {
        Channel channel = getBackendChannelIfActive(address);
        if (channel != null) {
            backendChannelMap.remove(address);
            channel.close();
        }
    }

    @Override
    public void removeBackendChannelWithoutClose(Address address) {
        backendChannelMap.remove(address);
    }

    @Override
    public Address removeBackendChannel(Channel channel) {
        for (Map.Entry<Address, Channel> entry : backendChannelMap.entrySet()) {
            if (entry.getValue() == channel) {
                removeBackendChannel(entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Channel getBackendChannel(Address address) {
        return backendChannelMap.get(address);
    }

    @Override
    public Channel getBackendChannelIfActive(Address address) {
        Channel channel = getBackendChannel(address);
        if (channel == null) {
            return null;
        }
        if (channel.isActive()) {
            return channel;
        } else {
            backendChannelMap.remove(address);
            return null;
        }
    }

    @Override
    public Set<Address> tryConnectBackendAddresses() {
        return null;
    }

    @Override
    public void addTryConnectBackendAddress(Address address) {

    }

    @Override
    public void removeTryConnectBackendAddress(Address address) {

    }

    @Override
    public int getBackendAddressTryConnectTimes(Address address) {
        return 0;
    }

    @Override
    public Set<Address> delayTryConnectBackendAddresses() {
        return null;
    }

    @Override
    public void addDelayTryConnectBackendAddress(Address address) {

    }

    @Override
    public void removeDelayTryConnectBackendAddress(Address address) {

    }
}
