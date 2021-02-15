package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface ProxyRouteService extends LifeCycle {

    RouteResult route(ProxyContext context);

    interface Aware {

        void setProxyRouteService(ProxyRouteService proxyRouteService);

        ProxyRouteService getProxyRouteService();
    }
}
