package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.ProxyRuleConfig;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.task.TaskExecutor;
import com.google.common.collect.Table;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class DefaultBackendTryConnectManager extends AbstractLifeCycle implements BackendTryConnectManager {

    private final FrpContext frpContext;

    private ConnectionManager connectionManager;

    private TaskExecutor taskExecutor;

    public DefaultBackendTryConnectManager(FrpContext frpContext) {
        this.frpContext = frpContext;
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        connectionManager = frpContext.getConnectionManager();
        taskExecutor = frpContext.getTaskExecutor();

        // 初始化后发起重连一次
        publishConfigThenTryConnectAll();
    }

    @Override
    public void tryConnect(String appName, String protocol, Address address) {
        ConnectionFactory connectionFactory = frpContext.getConnectionFactory(appName, protocol);
        if (connectionFactory == null) {
            return;
        }

        Channel channel = connectionManager.getBackendChannelIfActive(address);
        if (channel != null) {
            return;
        }

        int times = connectionManager.getBackendAddressTryConnectTimes(address);
        int maxTimes = SystemConfigs.BACKEND_TRY_CONNECT_TIMES.intValue();
        if (times > maxTimes) {
            log.info("address={} 超过最大连接次数={} 放弃连接", address, maxTimes);
            connectionManager.removeTryConnectBackendAddress(address);
            connectionManager.addDelayTryConnectBackendAddress(address);
            return;
        }

        taskExecutor.execute(null, () -> {
            try {
                Channel ch = connectionFactory.createConnection(address.getIp(), address.getPort(), 1000);
                if (ch != null) {
                    connectionManager.addBackendChannel(address, ch);
                    return;
                }
            } catch (Exception e) {
                log.warn("Backend连接失败：address={}", address);
            }

            tryConnect(appName, protocol, address);
        }, 1000);
    }

    @Override
    public synchronized void publishConfigThenTryConnectAll() {
        Set<Address> backendChannelAddresses = connectionManager.backendChannelAddresses();

        Table<String/*appName*/, String/*protocol*/, Set<Address>> availableAddresses = ProxyRuleConfig.getInstance().getAvailableAddresses();
        for (Table.Cell<String, String, Set<Address>> cell : availableAddresses.cellSet()) {
            ConnectionFactory connectionFactory = frpContext.getConnectionFactory(cell.getRowKey(), cell.getColumnKey());
            if (connectionFactory == null) {
                log.warn("应用{}，协议{}没有配置后端连接服务", cell.getRowKey(), cell.getColumnKey());
                return;
            }

            Optional.ofNullable(cell.getValue())
                    .orElse(Collections.emptySet())
                    .forEach(address -> {
                        tryConnect(cell.getRowKey(), cell.getColumnKey(), address);
                    });
        }
    }
}
