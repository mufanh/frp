package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.core.LifeCycle;
import io.netty.channel.Channel;

/**
 * 创建连接工厂（NETTY客户端）
 *
 * @author xinquan.huangxq
 */
public interface ConnectionFactory extends LifeCycle {

    class FeatureKeys {
        public static final String NETTY_BUFFER_LOW_WATERMARK = "NETTY_BUFFER_LOW_WATERMARK";
        public static final String NETTY_BUFFER_HIGH_WATERMARK = "NETTY_BUFFER_HIGH_WATERMARK";
    }

    /**
     * 创建连接
     *
     * @param targetIP
     * @param targetPort
     * @param connectTimeout
     * @return
     * @throws Exception
     */
    Channel createConnection(String targetIP, int targetPort, int connectTimeout) throws Exception;
}
