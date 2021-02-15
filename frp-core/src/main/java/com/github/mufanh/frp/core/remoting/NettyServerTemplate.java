package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.ConfigFeature;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.util.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public abstract class NettyServerTemplate extends AbstractLifeCycle {

    public static class FeatureKeys {
        public static final String TCP_SO_SNDBUF = "TCP_SO_SNDBUF";
        public static final String TCP_SO_RCVBUF = "TCP_SO_RCVBUF";

        public static final String NETTY_BUFFER_LOW_WATERMARK = "NETTY_BUFFER_LOW_WATERMARK";
        public static final String NETTY_BUFFER_HIGH_WATERMARK = "NETTY_BUFFER_HIGH_WATERMARK";
    }

    /**
     * 非daemon，需要考虑优雅下线
     */
    private final EventLoopGroup bossGroup = NettyEventLoopUtil.newEventLoopGroup(
            1,
            new NamedThreadFactory("Rpc-netty-server-boss", false));

    private static final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(
            Runtime.getRuntime().availableProcessors() * 2,
            new NamedThreadFactory("Rpc-netty-server-worker", true));

    private final String ip;

    private final int port;

    private final ConfigFeature configFeature;

    private final ChannelInitializer<SocketChannel> channelInitializer;

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    private ChannelFuture channelFuture;

    static {
        if (workerGroup instanceof NioEventLoopGroup) {
            ((NioEventLoopGroup) workerGroup).setIoRatio(SystemConfigs.NETTY_IO_RATIO.intValue());
        }
    }


    public NettyServerTemplate(ConfigFeature configFeature, String ip, int port, ChannelInitializer<SocketChannel> channelInitializer) {
        this.configFeature = configFeature;
        this.ip = ip;
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void start() throws LifeCycleException {
        try {
            super.start();

            doInit();

            if (doStart()) {
                log.warn("代理服务启动端口：{}", port);
            } else {
                log.warn("代理服务启动失败，端口：{}", port);
                throw new LifeCycleException("代理服务启动失败，端口：" + port);
            }
        } catch (Throwable t) {
            stop();
            throw new IllegalStateException("代理服务启动失败!", t);
        }
    }

    @Override
    public void stop() throws LifeCycleException {
        super.stop();

        if (!doStop()) {
            throw new LifeCycleException("代理服务停止失败");
        }
    }

    private boolean doStop() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        return true;
    }

    private boolean doStart() throws InterruptedException {
        channelFuture = this.bootstrap.bind(new InetSocketAddress(ip, port)).sync();
        return channelFuture.isSuccess();
    }

    private void doInit() {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopUtil.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG, SystemConfigs.TCP_SO_BACKLOG.intValue())
                .option(ChannelOption.SO_REUSEADDR, SystemConfigs.TCP_SO_REUSEADDR.boolValue())
                .childOption(ChannelOption.TCP_NODELAY, SystemConfigs.TCP_NODELAY.boolValue())
                .childOption(ChannelOption.SO_KEEPALIVE, SystemConfigs.TCP_SO_KEEPALIVE.boolValue())
                .childOption(ChannelOption.SO_SNDBUF, configFeature.getFeature(
                        FeatureKeys.TCP_SO_SNDBUF, SystemConfigs.TCP_SO_SNDBUF::getInt))
                .childOption(ChannelOption.SO_RCVBUF, configFeature.getFeature(
                        FeatureKeys.TCP_SO_RCVBUF, SystemConfigs.TCP_SO_RCVBUF::getInt));

        initWriteBufferWaterMark();

        if (SystemConfigs.NETTY_BUFFER_POOLED.boolValue()) {
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }

        NettyEventLoopUtil.enableTriggeredMode(bootstrap);

        bootstrap.childHandler(channelInitializer);
    }

    private void initWriteBufferWaterMark() {
        Integer lowWaterMark = configFeature.getFeature(FeatureKeys.NETTY_BUFFER_LOW_WATERMARK,
                SystemConfigs.NETTY_BUFFER_LOW_WATERMARK::getInt);
        Integer highWaterMark = configFeature.getFeature(FeatureKeys.NETTY_BUFFER_HIGH_WATERMARK,
                SystemConfigs.NETTY_BUFFER_HIGH_WATERMARK::getInt);
        if (lowWaterMark == null && highWaterMark == null) {
            return;
        } else if (lowWaterMark == null || highWaterMark == null) {
            throw new IllegalStateException("NETTY Server高低水位必须同时设置，否则使用默认值不显示设置");
        } else if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException("NETTY Server高低水位设置不正确");
        } else {
            log.warn("NETTY Server高低水位设置：high={}，low={}", highWaterMark, lowWaterMark);
        }
        this.bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                new WriteBufferWaterMark(lowWaterMark, highWaterMark));
    }
}
