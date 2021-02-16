package com.github.mufanh.plugins.protocol.tcp2;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * IP工具类
 *
 * @author xinquan.huangxq
 */
@Slf4j
public final class IPUtil {

    private IPUtil() {}

    public static String getChannelRemoteIP(ChannelHandlerContext ctx) {
        String remoteAddress = "";
        try {
            SocketAddress sockAddress = ctx.channel().remoteAddress();
            if (sockAddress instanceof InetSocketAddress) {
                InetAddress inetAddress = ((InetSocketAddress) sockAddress).getAddress();
                remoteAddress = inetAddress.getHostAddress();
            }
        } catch (Exception e) {
            log.error("获取远程地址失败", e);
        }
        return remoteAddress;
    }
}
