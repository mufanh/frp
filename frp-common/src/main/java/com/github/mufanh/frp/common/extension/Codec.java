package com.github.mufanh.frp.common.extension;

import io.netty.channel.ChannelHandler;
import org.pf4j.ExtensionPoint;

/**
 * @author xinquan.huangxq
 */
public interface Codec extends ExtensionPoint {

    ChannelHandler newEncoder();

    ChannelHandler newDecoder();
}
