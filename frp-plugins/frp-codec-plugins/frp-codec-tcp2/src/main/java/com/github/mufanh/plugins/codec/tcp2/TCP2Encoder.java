package com.github.mufanh.plugins.codec.tcp2;

import com.github.mufanh.frp.common.ProxyContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class TCP2Encoder extends MessageToByteEncoder<ProxyContext> {

    private final String charset;

    public TCP2Encoder(String charset) {
        super();
        this.charset = charset;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProxyContext context, ByteBuf out) throws Exception {
        try {
            if (StringUtils.isEmpty(context.getPayload())) {
                // 心跳包
                out.writeBytes("00000000".getBytes());
                return;
            }

            String body = context.getPayload();
            byte[] data = body.getBytes(charset);
            int length = data.length;
            DecimalFormat df = new DecimalFormat("00000000");
            String lengthStr = df.format(length);
            byte[] lengthField = lengthStr.getBytes();
            out.writeBytes(lengthField);
            out.writeBytes(data);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
