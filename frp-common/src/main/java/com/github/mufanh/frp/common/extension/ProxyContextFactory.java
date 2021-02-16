package com.github.mufanh.frp.common.extension;

/**
 * @author xinquan.huangxq
 */
public interface ProxyContextFactory {

    ProxyContext createHeartBeat();

    ProxyContext createProxyContext();

    ProxyContext createFailureProxyContext(ProxyContext requestProxyContext, String code, String message);
}
