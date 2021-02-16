package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyServerConfig;
import com.github.mufanh.frp.core.config.ProxyRuleConfig;
import com.google.common.collect.Table;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class BackendConnectHandler extends AbstractHeartBeatHandler {

    private final ProxyServerConfig proxyServerConfig;

    private final ConnectionManager connectionManager;

    private final BackendTryConnectManager backendTryConnectManager;

    public BackendConnectHandler(FrpContext frpContext, ProxyServerConfig proxyServerConfig) {
        super(frpContext, proxyServerConfig);

        this.proxyServerConfig = proxyServerConfig;
        this.connectionManager = frpContext.getConnectionManager();
        this.backendTryConnectManager = frpContext.getBackendTryConnectManager();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        retryConnect(ctx.channel());
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.error("Backend通讯异常：{}, {}", cause.getMessage(), ctx.channel().remoteAddress());
        } else if (cause instanceof DecoderException) {
            Throwable c = cause.getCause();
            if (c != null) {
                cause = c;
            }
            log.error("Backend解码异常：{}, {}", cause.getMessage(), ctx.channel().remoteAddress());
        } else {
            log.error("Backend未捕获异常：{}", ctx.channel().remoteAddress(), cause);
        }

        retryConnect(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            if (channel == null) {
                return;
            }

            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                retryConnect(channel);
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                // 处理读+ALL即可实现心跳，忽略WRITE_IDLE
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                if (channel.isActive() && channel.isWritable()) {
                    log.info("Backend发起心跳：{}", channel.remoteAddress());
                    try {

                        channel.writeAndFlush(createHeartBeat()).addListener((ChannelFuture future) -> {
                            if (!future.isSuccess()) {
                                retryConnect(channel);
                            }
                        });
                    } catch (Throwable e) {
                        log.error("Backend心跳发送异常，断开连接，然后重连", e);

                        retryConnect(channel);
                    }
                }
            }
        }
    }

    private void retryConnect(Channel channel) {
        Address address = connectionManager.removeBackendChannel(channel);
        if (address != null) {
            Table<String/*appName*/, String/*protocol*/, Set<Address>> table =
                    ProxyRuleConfig.getInstance().getAvailableAddresses();
            Set<Address> availableAddresses = table.get(proxyServerConfig.getAppName(), proxyServerConfig.getProtocol());
            if (availableAddresses != null && availableAddresses.contains(address)) {
                backendTryConnectManager.tryConnect(proxyServerConfig.getAppName(), proxyServerConfig.getProtocol(), address);
            }
        }
    }
}
