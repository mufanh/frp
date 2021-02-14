package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.util.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class AbstractConnectionFactory extends AbstractLifeCycle implements ConnectionFactory {

    private static final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(
            Runtime.getRuntime().availableProcessors() + 1,
            new NamedThreadFactory("Netty-client-worker", true));

    protected final Bootstrap bootstrap = new Bootstrap();

    private final ConfigFeature configFeature;

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public AbstractConnectionFactory(final ConfigFeature configFeature,
                                     final ChannelInitializer<SocketChannel> channelInitializer) {
        this.configFeature = configFeature;
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        doStart();
    }

    private void doStart() {
        bootstrap.group(workerGroup)
                .channel(NettyEventLoopUtil.getClientSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, SystemConfigs.TCP_NODELAY.boolValue())
                .option(ChannelOption.SO_REUSEADDR, SystemConfigs.TCP_SO_REUSEADDR.boolValue())
                .option(ChannelOption.SO_KEEPALIVE, SystemConfigs.TCP_SO_KEEPALIVE.boolValue())
                .option(ChannelOption.SO_SNDBUF, SystemConfigs.TCP_SO_SNDBUF.getInt())
                .option(ChannelOption.SO_RCVBUF, SystemConfigs.TCP_SO_RCVBUF.getInt());

        initWriteBufferWaterMark();

        if (SystemConfigs.NETTY_BUFFER_POOLED.boolValue()) {
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }

        bootstrap.handler(channelInitializer);
    }

    @Override
    public Connection createConnection(String targetIP, int targetPort, int connectTimeout) throws Exception {
        ensureStarted();

        Channel channel = doCreateConnection(targetIP, targetPort, connectTimeout);
        return new Connection(channel);
    }

    protected Channel doCreateConnection(String targetIP, int targetPort, int connectTimeout) throws Exception {
        // prevent unreasonable value, at least 1000
        connectTimeout = Math.max(connectTimeout, 1000);
        String address = targetIP + ":" + targetPort;

        log.debug("connectTimeout of address [{}] is [{}].", address, connectTimeout);

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "创建连接超时，连接地址： " + address;
            log.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "连接创建过程中被人主动撤销，连接地址：" + address;
            log.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "创建连接失败，连接地址：" + address;
            log.warn(errMsg);
            throw new Exception(errMsg, future.cause());
        }
        return future.channel();
    }

    private void initWriteBufferWaterMark() {
        Integer lowWaterMark = configFeature.getFeature(FeatureKeys.NETTY_BUFFER_LOW_WATERMARK,
                SystemConfigs.NETTY_BUFFER_LOW_WATERMARK::getInt);
        Integer highWaterMark = configFeature.getFeature(FeatureKeys.NETTY_BUFFER_HIGH_WATERMARK,
                SystemConfigs.NETTY_BUFFER_HIGH_WATERMARK::getInt);
        if (lowWaterMark == null && highWaterMark == null) {
            return;
        } else if (lowWaterMark == null || highWaterMark == null) {
            throw new IllegalStateException("NETTY Client高低水位必须同时设置，否则使用默认值不显示设置");
        } else if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException("NETTY Client高低水位设置不正确");
        } else {
            log.warn("NETTY Client高低水位设置：high={}，low={}", highWaterMark, lowWaterMark);
        }
        this.bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK,
                new WriteBufferWaterMark(lowWaterMark, highWaterMark));
    }
}
