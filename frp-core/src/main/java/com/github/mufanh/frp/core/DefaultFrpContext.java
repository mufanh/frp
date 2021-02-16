package com.github.mufanh.frp.core;

import com.github.mufanh.frp.core.config.ProxyServerConfig;
import com.github.mufanh.frp.core.config.ProxyRuleConfig;
import com.github.mufanh.frp.core.extension.DefaultExtensionManager;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import com.github.mufanh.frp.core.remoting.*;
import com.github.mufanh.frp.core.service.*;
import com.github.mufanh.frp.core.task.DefaultTaskExecutor;
import com.github.mufanh.frp.core.task.TaskExecutor;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author xinquan.huangxq
 */
public class DefaultFrpContext extends AbstractLifeCycle implements FrpContext {

    @Getter
    @Setter
    private ExtensionManager extensionManager;

    @Getter
    @Setter
    private ConnectionManager connectionManager;

    @Getter
    @Setter
    private InvokeManager invokeManager;

    @Getter
    @Setter
    private ProxyInvokeService proxyInvokeService;

    @Getter
    @Setter
    private ChannelChooseService channelChooseService;

    @Getter
    @Setter
    private ChannelHealthCheck channelHealthCheck;

    @Getter
    @Setter
    private ProxyRouteService proxyRouteService;

    @Getter
    @Setter
    private ProxySelectService proxySelectService;

    @Getter
    @Setter
    private TaskExecutor taskExecutor;

    @Getter
    @Setter
    private BackendTryConnectManager backendTryConnectManager;

    private final Table<String, String, ProxyServerConfig> proxyConfigTable = HashBasedTable.create();

    private final Table<String, String, BackendConnectionFactory> backendConnectionFactoryTable = HashBasedTable.create();

    private final Table<String, String, FrontendProxyServer> frontendProxyServerTable = HashBasedTable.create();

    public DefaultFrpContext() {
        this.extensionManager = new DefaultExtensionManager();
        this.connectionManager = new DefaultConnectionManager();
        this.invokeManager = new DefaultInvokeManager();
        this.proxyInvokeService = new DefaultProxyInvokeService(this);
        this.channelChooseService = new DefaultChannelChooseService(this);
        this.channelHealthCheck = new DefaultChannelHealthCheck(this);
        this.proxyRouteService = new DefaultProxyRouteService(this);
        this.proxySelectService = new DefaultProxySelectService();
        this.taskExecutor = new DefaultTaskExecutor();
        this.backendTryConnectManager = new DefaultBackendTryConnectManager(this);
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        proxyInvokeService.start();
        channelChooseService.start();
        channelHealthCheck.start();
        proxyRouteService.start();
        taskExecutor.start();

        frontendProxyServerTable.values()
                .forEach(LifeCycle::start);

        backendConnectionFactoryTable.values()
                .forEach(LifeCycle::start);

        backendTryConnectManager.start();
    }

    @Override
    public void addProxyService(ProxyServerConfig proxyServerConfig) {
        ensureNotStarted();

        proxyConfigTable.put(proxyServerConfig.getAppName(), proxyServerConfig.getProtocol(), proxyServerConfig);

        frontendProxyServerTable.put(proxyServerConfig.getAppName(), proxyServerConfig.getProtocol(),
                new FrontendProxyServer(proxyServerConfig, this));
        backendConnectionFactoryTable.put(proxyServerConfig.getAppName(), proxyServerConfig.getProtocol(),
                new BackendConnectionFactory(proxyServerConfig, this));

        // 默认地址列表
        if (CollectionUtils.isNotEmpty(proxyServerConfig.getDefaultAddresses())) {
            ProxyRuleConfig.getInstance().setDefaultConfig(
                    proxyServerConfig.getAppName(), proxyServerConfig.getProtocol(), proxyServerConfig.getDefaultAddresses());
        }
    }

    @Override
    public ConnectionFactory getConnectionFactory(String appName, String protocol) {
        return backendConnectionFactoryTable.get(appName, protocol);
    }

    @Override
    public ProxyServerConfig getProxyConfig(String appName, String protocol) {
        return proxyConfigTable.get(appName, protocol);
    }
}
