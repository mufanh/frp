package com.github.mufanh.frp.core;

import com.github.mufanh.frp.core.config.ProxyConfig;
import com.github.mufanh.frp.core.extension.ExtensionManagerAware;
import com.github.mufanh.frp.core.task.TaskExecutorAware;

/**
 * Frp运行上下文（各类配置、各类组件容器）
 *
 * @author xinquan.huangxq
 */
public interface FrpContext extends ExtensionManagerAware, TaskExecutorAware, LifeCycle {

    void addProxyService(ProxyConfig proxyConfig);
}
