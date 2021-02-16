package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.ErrCode;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.ProxyException;
import com.github.mufanh.frp.core.FrpContext;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class BackendProxyHandler extends ChannelDuplexHandler {

    private final InvokeManager invokeManager;

    private final ConnectionManager connectionManager;

    public BackendProxyHandler(final FrpContext frpContext) {
        this.invokeManager = frpContext.getInvokeManager();
        this.connectionManager = frpContext.getConnectionManager();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ProxyContext)) {
            throw new Exception("错误的消息类型");
        }

        ProxyContext responseProxyContext = (ProxyContext) msg;

        // 心跳响应
        if (responseProxyContext.isHeartBeat()) {
            log.info("Backend收到心跳:{}", ctx.channel().remoteAddress());
            return;
        }

        if (StringUtils.isBlank(responseProxyContext.getMsgId())) {
            throw new ProxyException(ErrCode.PROXY_BACKEND_ERROR, "代理服务响应报文不合法，丢失消息唯一标识");
        }

        ProxyContext requestProxyContext = invokeManager.removeInvokeContext(responseProxyContext.getMsgId());
        if (requestProxyContext == null) {
            // 服务超时，可以已经释放了
            return;
        }

        ChannelId frontendChannelId = requestProxyContext.getHeader(ExchangeProxyContext.HEADER_CHANNEL_ID);
        if (frontendChannelId == null) {
            return;
        }
        Channel channel = connectionManager.getFrontendChannelIfActive(frontendChannelId);
        if (channel != null) {
            channel.writeAndFlush(responseProxyContext).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    // 添加日志
                } else {
                    // 写响应报文异常，关闭上游连接
                    future.channel().close();
                }
            });
        }
    }
}
