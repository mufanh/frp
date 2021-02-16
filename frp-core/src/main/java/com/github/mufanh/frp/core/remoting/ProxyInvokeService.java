package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.ProxyException;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface ProxyInvokeService extends LifeCycle {

    void invoke(final ExchangeProxyContext context) throws ProxyException;

    interface Aware {
        void setProxyInvokeService(ProxyInvokeService proxyInvokeService);

        ProxyInvokeService getProxyInvokeService();
    }
}
