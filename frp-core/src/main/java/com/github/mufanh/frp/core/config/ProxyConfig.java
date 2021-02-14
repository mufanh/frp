package com.github.mufanh.frp.core.config;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class ProxyConfig {

    private String appName;

    private String protocol;

    private String ip;

    private int port;

    private String codecPluginId;

    private String codecType;

    private long timeout;

    private Integer frontendTcpSoSndBuf;

    private Integer frontendTcpSoRcvBuf;

    private Integer frontendNettyBufferHighWatermark;

    private Integer frontendNettyBufferLowWatermark;

    private Integer frontendAccessIdleTime;

    private Integer backendNettyBufferHighWatermark;

    private Integer backendNettyBufferLowWatermark;

    /**
     * Backend长连接读超时时间（秒）
     */
    private Integer backendReadIdleTime;

    /**
     * Backend长连接写超时时间（秒）
     */
    private Integer backendWriteIdleTime;

    /**
     * Backend独写空闲时间（秒）
     */
    private Integer backendAccessIdleTime;
}
