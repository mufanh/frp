package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface ProxyRouteService extends LifeCycle {

    RouteResult route(ExchangeProxyContext context);

    interface Aware {

        void setProxyRouteService(ProxyRouteService proxyRouteService);

        ProxyRouteService getProxyRouteService();
    }
}
