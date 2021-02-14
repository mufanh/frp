package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.FrpContext;
import io.netty.channel.ChannelDuplexHandler;

/**
 * @author xinquan.huangxq
 */
public class FrontendProxyHandler extends ChannelDuplexHandler {

    private final FrpContext frpContext;

    public FrontendProxyHandler(FrpContext frpContext) {
        this.frpContext = frpContext;
    }
}
