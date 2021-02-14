package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.ErrCode;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.ProxyException;
import com.github.mufanh.frp.core.util.TimerHolder;
import com.google.common.collect.Maps;
import io.netty.util.Timeout;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xinquan.huangxq
 */
public class DefaultInvokeManager implements InvokeManager {

    private final Map<String/*msgId*/, InvokeContext> invokeContextMap = Maps.newConcurrentMap();

    @Override
    public void addInvokeContext(ProxyContext context, long delay) {
        Timeout timeout = TimerHolder.getTimer().newTimeout(t -> {
            invokeContextMap.remove(context.getMsgId());

            context.getExceptionHandler().handleException(
                    context, new ProxyException(ErrCode.PROXY_TIMEOUT));
        }, delay, TimeUnit.MILLISECONDS);

        InvokeContext invokeContext = new InvokeContext(context, timeout);
        invokeContextMap.putIfAbsent(context.getMsgId(), invokeContext);
    }

    @Override
    public void removeInvokeContext(String msgId) {
        InvokeContext invokeContext = invokeContextMap.remove(msgId);
        if (invokeContext != null) {
            invokeContext.getTimeout().cancel();
        }
    }

    @Getter
    private static final class InvokeContext {

        private final ProxyContext context;

        private final Timeout timeout;

        public InvokeContext(ProxyContext context, Timeout timeout) {
            this.context = context;
            this.timeout = timeout;
        }
    }
}
