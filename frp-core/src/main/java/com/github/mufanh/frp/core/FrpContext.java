package com.github.mufanh.frp.core;

import com.github.mufanh.frp.core.config.ProxyServerConfig;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import com.github.mufanh.frp.core.remoting.*;
import com.github.mufanh.frp.core.service.ChannelChooseService;
import com.github.mufanh.frp.core.service.ChannelHealthCheck;
import com.github.mufanh.frp.core.service.ProxyRouteService;
import com.github.mufanh.frp.core.service.ProxySelectService;
import com.github.mufanh.frp.core.task.TaskExecutor;

/**
 * Frp运行上下文（各类配置、各类组件容器）
 *
 * @author xinquan.huangxq
 */
public interface FrpContext extends LifeCycle
        , TaskExecutor.Aware
        , ConnectionManager.Aware
        , ProxyInvokeService.Aware
        , ProxyRouteService.Aware
        , InvokeManager.Aware
        , ChannelChooseService.Aware
        , ProxySelectService.Aware
        , ChannelHealthCheck.Aware
        , BackendTryConnectManager.Aware
        , ExtensionManager.Aware {

    void addProxyService(ProxyServerConfig proxyServerConfig);

    ConnectionFactory getConnectionFactory(String appName, String protocol);

    ProxyServerConfig getProxyConfig(String appName, String protocol);
}
