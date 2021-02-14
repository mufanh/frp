package com.github.mufanh.frp.core.remoting;

import io.netty.channel.Channel;

/**
 * @author xinquan.huangxq
 */
public class Connection {

    private final Channel channel;

    public Connection(final Channel channel) {
        this.channel = channel;
    }
}
