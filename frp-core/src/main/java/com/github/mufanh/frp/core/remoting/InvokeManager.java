package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.ProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface InvokeManager {

    void addInvokeContext(ProxyContext context, long delay);

    void removeInvokeContext(String msgId);

    interface Aware {

        void setInvokeManager(InvokeManager invokeManager);

        InvokeManager getInvokeManager();
    }
}
