package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.ProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface ProxyRouteService {

    RouteResult route(ProxyContext context);

    interface Aware {

        void setProxyRouteService(ProxyRouteService proxyRouteService);

        ProxyRouteService getProxyRouteService();
    }
}
