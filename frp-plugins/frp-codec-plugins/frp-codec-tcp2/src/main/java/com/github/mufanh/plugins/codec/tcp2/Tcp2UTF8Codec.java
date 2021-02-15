package com.github.mufanh.plugins.codec.tcp2;

import com.github.mufanh.frp.common.extension.Codec;
import io.netty.channel.ChannelHandler;
import org.pf4j.Extension;

/**
 * @author xinquan.huangxq
 */
@Extension
public class Tcp2UTF8Codec implements Codec {

    @Override
    public ChannelHandler newEncoder() {
        return new TCP2Encoder("UTF-8");
    }

    @Override
    public ChannelHandler newDecoder() {
        return new TCP2Decoder("UTF-8");
    }
}
