package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.FrpContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class FrontendConnectHandler extends ChannelDuplexHandler {

    private final ConnectionManager connectionManager;

    public FrontendConnectHandler(FrpContext frpContext) {
        this.connectionManager = frpContext.getConnectionManager();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        connectionManager.addFrontendChannel(channel);

        log.info("收到连接请求：{}，连接标识：{}", ctx.channel().remoteAddress(), channel.id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("连接已关闭：{}，连接标识：{}", ctx.channel().remoteAddress(), ctx.channel().id());

        closeOnFlush(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.error("Fronted通讯异常：{}, {}，连接标识：{}", cause.getMessage(), ctx.channel().remoteAddress(), ctx.channel().id());
        } else if (cause instanceof DecoderException) {
            Throwable c = cause.getCause();
            if (c != null) {
                cause = c;
            }
            log.error("Fronted解码异常：{}, {}，连接标识：{}", cause.getMessage(), ctx.channel().remoteAddress(), ctx.channel().id());
        } else {
            log.error("Fronted未捕获异常：{}, 连接标识：{}", ctx.channel().remoteAddress(), ctx.channel().id(), cause);
        }

        closeOnFlush(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("Fronted连接超时，强制关闭：{}，连接标识：{}", ctx.channel().remoteAddress(), ctx.channel().id());

            closeOnFlush(ctx.channel());
        }
    }

    /**
     * 关闭连接记得删掉关联信息
     *
     * @param channel
     */
    private void closeOnFlush(Channel channel) {
        connectionManager.removeFrontendChannel(channel.id());

        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
