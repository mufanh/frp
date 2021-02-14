package com.github.mufanh.frp.core;

import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import com.github.mufanh.frp.core.remoting.ConnectionFactory;
import com.github.mufanh.frp.core.remoting.ConnectionManager;
import com.github.mufanh.frp.core.remoting.InvokeManager;
import com.github.mufanh.frp.core.remoting.ProxyInvokeService;
import com.github.mufanh.frp.core.service.ProxyRouteService;
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
        , ExtensionManager.Aware {

    void addProxyService(ProxyConfig proxyConfig);

    ConnectionFactory getConnectionFactory(String appName, String protocol);

    ProxyConfig getProxyConfig(String appName, String protocol);
}
