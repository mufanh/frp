package com.github.mufanh.frp.core.remoting;

/**
 * @author xinquan.huangxq
 */
public interface ProxyFutureManagerAware {

    void setProxyFutureManager(ProxyFutureManager proxyFutureManager);

    ProxyFutureManager getProxyFutureManager();
}
