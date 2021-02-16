package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.extension.ProxyContext;
import com.github.mufanh.frp.core.ExchangeProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface InvokeManager {

    void addInvokeContext(ExchangeProxyContext context, long delay);

    ProxyContext removeInvokeContext(String msgId);

    interface Aware {

        void setInvokeManager(InvokeManager invokeManager);

        InvokeManager getInvokeManager();
    }
}
