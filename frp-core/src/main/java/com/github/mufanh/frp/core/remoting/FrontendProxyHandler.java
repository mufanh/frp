package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.AbstractExceptionHandler;
import com.github.mufanh.frp.core.ErrCode;
import com.github.mufanh.frp.core.ExceptionHandler;
import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.task.TaskExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xinquan.huangxq
 */
@Slf4j
@ChannelHandler.Sharable
public class FrontendProxyHandler extends AbstractProxyHandler {

    private final ConnectionManager connectionManager;

    private final ProxyInvokeService proxyInvokeService;

    private final TaskExecutor taskExecutor;

    public FrontendProxyHandler(FrpContext frpContext, ProxyConfig proxyConfig) {
        super(frpContext, proxyConfig);

        this.connectionManager = frpContext.getConnectionManager();
        this.proxyInvokeService = frpContext.getProxyInvokeService();
        this.taskExecutor = frpContext.getTaskExecutor();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ExchangeProxyContext)) {
            throw new Exception("未定义的消息类型");
        }

        ExchangeProxyContext context = (ExchangeProxyContext) msg;
        context.setHeader(ExchangeProxyContext.HEADER_CHANNEL_ID, ctx.channel().id());

        if (context.isHeartBeat()) {
            log.info("Fronted收到心跳:{}", ctx.channel().remoteAddress());
            // 心跳应答
            ctx.writeAndFlush(createHeartBeat());
            return;
        }

        taskExecutor.executeImmediately(context, () -> {
            ExceptionHandler exceptionHandler = context.getExceptionHandler();
            context.setExceptionHandler(new AbstractExceptionHandler() {
                @Override
                protected void response(ExchangeProxyContext context) {
                    if (exceptionHandler != null) {
                        exceptionHandler.handleException(context, context.getException());
                    }

                    if (needCloseChannel(context)) {
                        Channel channel = connectionManager.removeFrontendChannel(ctx.channel().id());
                        channel.close();
                    }
                }

                /**
                 * 是否需要断开连接
                 * @param context
                 * @return
                 */
                private boolean needCloseChannel(ExchangeProxyContext context) {
                    String errCode = context.getCode();
                    if (errCode == null) {
                        return false;
                    }
                    if (errCode.equals(ErrCode.PROXY_BAD_REQUEST.code())) {
                        return true;
                    }
                    return false;
                }
            });

            // 执行代理服务
            proxyInvokeService.invoke(context);
        });
    }
}
