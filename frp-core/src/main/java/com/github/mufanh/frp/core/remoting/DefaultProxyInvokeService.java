package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.*;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.service.ProxyRouteService;
import com.github.mufanh.frp.core.service.RouteResult;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class DefaultProxyInvokeService implements ProxyInvokeService {

    private final FrpContext frpContext;

    private final ProxyRouteService proxyRouteService;

    private final ConnectionManager connectionManager;

    private final InvokeManager invokeManager;

    public DefaultProxyInvokeService(final FrpContext frpContext) {
        this.frpContext = frpContext;
        this.proxyRouteService = frpContext.getProxyRouteService();
        this.connectionManager = frpContext.getConnectionManager();
        this.invokeManager = frpContext.getInvokeManager();
    }

    public void invoke(final ProxyContext context) throws ProxyException {
        RouteResult routeResult = proxyRouteService.route(context);
        if (!routeResult.isSuccess()) {
            throw new ProxyException(ErrCode.PROXY_NONE_SERVICE, "服务代理找不到可用的服务主机");
        }

        proxyInvoke(context, routeResult.getAddress());
    }

    private void proxyInvoke(ProxyContext context, Address address) throws ProxyException {
        Channel channel = connectionManager.getBackendChannel(address);
        if (channel != null) {
            sendToBackendChannel(channel, context);
            return;
        }

    }

    private void sendToBackendChannel(Channel channel, ProxyContext context) {
        ProxyConfig proxyConfig = frpContext.getProxyConfig(context.getAppName(), context.getProtocol());

        invokeManager.addInvokeContext(context, proxyConfig.getTimeout());

        ExceptionHandler exceptionHandler = context.getExceptionHandler();
        context.setExceptionHandler(new AbstractExceptionHandler() {
            @Override
            protected void response(ProxyContext context) {
                // 删除请求
                invokeManager.removeInvokeContext(context.getMsgId());

                if (exceptionHandler != null) {
                    exceptionHandler.handleException(context, context.getException());
                }
            }
        });

        channel.writeAndFlush(context.getPayload()).addListener((ChannelFutureListener) cf -> {
            if (!cf.isSuccess()) {
                invokeManager.removeInvokeContext(context.getMsgId());

                if (context.getExceptionHandler() != null) {
                    context.getExceptionHandler().handleException(context,
                            new ProxyException(ErrCode.PROXY_ROUTE_ERROR, "代理服务请求异常"));
                }

                // 关闭连接，然后发起重连
                channel.close();
            }
        });
    }
}