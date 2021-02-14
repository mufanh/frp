package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.ProxyContext;
import io.netty.util.Timeout;

/**
 * @author xinquan.huangxq
 */
public interface ProxyFuture {

    void putResponse(final ProxyContext context);

    void cancelTimeout();

    void addTimeout(Timeout timeout);

    boolean isDone();
}
