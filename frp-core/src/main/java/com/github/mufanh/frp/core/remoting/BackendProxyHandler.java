package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.ErrCode;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.ProxyException;
import com.github.mufanh.frp.core.FrpContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class BackendProxyHandler extends ChannelDuplexHandler {

    private final ProxyFutureManager proxyFutureManager;

    public BackendProxyHandler(final FrpContext frpContext) {
        this.proxyFutureManager = frpContext.getProxyFutureManager();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ProxyContext)) {
            throw new Exception("错误的消息类型");
        }

        ProxyContext context = (ProxyContext) msg;
        if (StringUtils.isBlank(context.getMsgId())) {
            throw new ProxyException(ErrCode.PROXY_BACKEND_ERROR, "代理服务响应报文不合法，丢失消息唯一标识");
        }

        ProxyFuture proxyFuture = proxyFutureManager.removeProxyFuture(context.getMsgId());
        proxyFuture.cancelTimeout();
        proxyFuture.putResponse(context);
    }
}