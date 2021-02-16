package com.github.mufanh.frp.core.config;

import com.github.mufanh.frp.common.Address;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
@Data
@Builder
public class ProxyConfig {

    private String appName;

    private String protocol;

    private String ip;

    private int port;

    private String protocolPluginId;

    private String protocolType;

    private String loadBalancePluginId;

    private String loadBalanceType;

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

    /**
     * 默认服务列表
     */
    private List<Address> defaultAddresses;
}
