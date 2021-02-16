package com.github.mufanh.plugins.protocol.tcp2;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.ProxyContextFactory;
import io.netty.channel.ChannelHandler;
import org.pf4j.Extension;

/**
 * @author xinquan.huangxq
 */
@Extension
public class Tcp2GBKProtocol implements Protocol {

    @Override
    public ChannelHandler newEncoder() {
        return new TCP2Encoder("GBK");
    }

    @Override
    public ChannelHandler newDecoder() {
        return new TCP2Decoder("GBK");
    }

    @Override
    public ProxyContextFactory newProxyContextFactory() {
        return new TCP2ProxyContextFactory();
    }
}
