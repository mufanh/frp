package com.github.mufanh.frp.common.extension;

import io.netty.channel.ChannelHandler;
import org.pf4j.ExtensionPoint;

/**
 * @author xinquan.huangxq
 */
public interface Protocol extends ExtensionPoint {

    /**
     * 编码器
     *
     * @return
     */
    ChannelHandler newEncoder();

    /**
     * 解码器
     *
     * @return
     */
    ChannelHandler newDecoder();

    /**
     * 上下文创建工厂
     *
     * @return
     */
    ProxyContextFactory newProxyContextFactory();
}
