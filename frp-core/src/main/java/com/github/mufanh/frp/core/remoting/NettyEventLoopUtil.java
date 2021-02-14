package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.config.SystemConfigs;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

/**
 * @author xinquan.huangxq
 */
public final class NettyEventLoopUtil {

    private static final boolean epollEnabled = SystemConfigs.NETTY_EPOLL_SWITCH.boolValue()
            && Epoll.isAvailable();

    public static EventLoopGroup newEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        return epollEnabled ? new EpollEventLoopGroup(nThreads, threadFactory)
                : new NioEventLoopGroup(nThreads, threadFactory);
    }

    public static Class<? extends SocketChannel> getClientSocketChannelClass() {
        return epollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static void enableTriggeredMode(ServerBootstrap serverBootstrap) {
        if (!epollEnabled) {
            return;
        }
        if (SystemConfigs.NETTY_EPOLL_LT.boolValue()) {
            serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);
        } else {
            serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }
    }

    private NettyEventLoopUtil() {
    }
}
