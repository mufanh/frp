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
public class ProxyServerConfig {

    /**
     * 代理应用名
     */
    private String appName;

    /**
     * 代理应用协议标识（appName+protocol定义具体的服务端口）
     */
    private String protocol;

    /**
     * 服务绑定IP
     */
    private String ip;

    /**
     * 服务绑定端口
     */
    private int port;

    /**
     * 服务所属插件ID（服务由第三方插件加载，定义插件ID）
     */
    private String protocolPluginId;

    /**
     * 服务类型（插件定义的包路径全名）
     */
    private String protocolType;

    /**
     * 负载类型所属插件ID（默认使用随机，可以不定义）
     */
    private String loadBalancePluginId;

    /**
     * 负载类型
     */
    private String loadBalanceType;

    /**
     * 代理超时时间
     */
    private long proxyInvokeTimeout;

    /**
     * 前端连接 SO_SNDBUF
     */
    private Integer frontendTcpSoSndBuf;

    /**
     * 前端连接 SO_RCVBUF
     */
    private Integer frontendTcpSoRcvBuf;

    /**
     * 前端连接 NETTY高水位
     */
    private Integer frontendNettyBufferHighWatermark;

    /**
     * 前端连接 NETTY低水位（高低水位可以使用默认值，要设置必须同时设置）
     */
    private Integer frontendNettyBufferLowWatermark;

    /**
     * 心跳超时时间
     */
    private Integer frontendAccessIdleTime;

    /**
     * 后端连接 NETTY高水位
     */
    private Integer backendNettyBufferHighWatermark;

    /**
     * 后端连接 NETTY低水位（可以使用默认值，要设置必须同时设置）
     */
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
     * 代理服务若没有命中任何规则，使用下述默认服务地址列表
     */
    private List<Address> defaultAddresses;
}
