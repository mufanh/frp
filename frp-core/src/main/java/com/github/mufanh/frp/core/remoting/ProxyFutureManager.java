package com.github.mufanh.frp.core.remoting;

/**
 * @author xinquan.huangxq
 */
public interface ProxyFutureManager {

    ProxyFuture addProxyFuture(ProxyFuture future);

    ProxyFuture getProxyFuture(String msgId);

    ProxyFuture removeProxyFuture(String msgId);
}
