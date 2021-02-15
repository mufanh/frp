package com.github.mufanh.plugins.codec.tcp2;

import com.github.mufanh.frp.common.ErrCode;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.ProxyException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public class TCP2Decoder extends ByteToMessageDecoder {

    private final String charset;

    public TCP2Decoder(String charset) {
        super();
        this.charset = charset;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {
            return;
        }

        in.markReaderIndex();
        byte[] lengthField = new byte[8];
        in.readBytes(lengthField);
        String lengthStr = new String(lengthField);
        int length = Integer.parseInt(lengthStr);
        if (length > 10 * 1024) {
            throw new ProxyException(ErrCode.PROXY_BAD_REQUEST, "报文过长");
        }

        if (length == 0) {
            // 心跳包
            out.add(ProxyContext.HEARTBEAT);
            return;
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[length];
        in.readBytes(data);

        String jsonStr = new String(data, charset);
        String ip = IPUtil.getChannelRemoteIP(ctx);

        Map<String, Object> json = JSONUtil.json2Map(jsonStr);

        ProxyContext context = new ProxyContext();
        context.setPayload(jsonStr);
        context.setMsgId((String) json.get("msgId"));
        context.setHeader(ProxyContext.HeaderKeys.IP, ip);

        for (Map.Entry<String, Object> entry : json.entrySet()) {
            if (entry.getValue() instanceof String) {
                context.addParam(entry.getKey(), (String) entry.getValue());
            }
        }

        out.add(context);
    }

}
