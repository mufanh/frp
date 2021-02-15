package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.ProxyException;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface ProxyInvokeService extends LifeCycle {

    void invoke(final ProxyContext context) throws ProxyException;

    interface Aware {
        void setProxyInvokeService(ProxyInvokeService proxyInvokeService);

        ProxyInvokeService getProxyInvokeService();
    }
}
