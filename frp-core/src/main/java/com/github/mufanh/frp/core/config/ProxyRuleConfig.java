package com.github.mufanh.frp.core.config;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.RuleGroup;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class ProxyRuleConfig {

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    // 整体进行替换，运行过程中会重新构造
    private Table<String/*appName*/, String/*protocol*/, List<RuleGroup>> config;

    // 分批进行替换
    // 仅启动时候会加载初始化一次
    private final Table<String/*appName*/, String/*protocol*/, List<Address>> defaultConfig = HashBasedTable.create();

    public void setDefaultConfig(String appName, String protocol, List<Address> addresses) {
        if (StringUtils.isBlank(appName)
                || StringUtils.isBlank(protocol)
                || CollectionUtils.isEmpty(addresses)) {
            log.warn("设置应用、协议默认服务地址列表信息不合法，忽略");
            return;
        }
        try {
            rwl.writeLock().lock();
            defaultConfig.put(appName, protocol,
                    // 进行一次排重，提高因为人为原因配置了连不同的地址，会出现多判断
                    ImmutableList.copyOf(addresses.stream()
                            .distinct()
                            .collect(Collectors.toList())));
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public List<Address> getDefaultConfig(String appName, String protocol) {
        if (StringUtils.isBlank(appName) || StringUtils.isBlank(protocol)) {
            return Collections.emptyList();
        }
        try {
            rwl.readLock().lock();
            return defaultConfig.get(appName, protocol);
        } finally {
            rwl.readLock().unlock();
        }
    }

    public void clearDefaultConfig() {
        try {
            rwl.writeLock().lock();
            defaultConfig.clear();
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public List<RuleGroup> getConfig(String appName, String protocol) {
        try {
            rwl.readLock().lock();

            if (config == null) {
                return Collections.emptyList();
            }

            List<RuleGroup> result = config.get(appName, protocol);
            if (result == null) {
                return Collections.emptyList();
            }

            return result;
        } finally {
            rwl.readLock().unlock();
        }
    }

    public void setConfig(List<RuleGroup> ruleGroups) {
        try {
            rwl.writeLock().lock();

            if (CollectionUtils.isEmpty(ruleGroups)) {
                config = null;
                return;
            }
        } finally {
            rwl.writeLock().unlock();
        }

        Table<String, String, List<RuleGroup>> tmpConfig = HashBasedTable.create();
        for (RuleGroup ruleGroup : ruleGroups) {
            List<RuleGroup> groups = tmpConfig.get(ruleGroup.getAppName(), ruleGroup.getProtocol());
            if (groups == null) {
                groups = new ArrayList<>();
                tmpConfig.put(ruleGroup.getAppName(), ruleGroup.getProtocol(), groups);
            }
            groups.add(ruleGroup);
        }

        try {
            rwl.writeLock().lock();
            config = tmpConfig;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    /**
     * 获取需要发起主动连接的地址列表
     *
     * @return
     */
    public Table<String/*appName*/, String/*protocol*/, Set<Address>> getAvailableAddresses() {
        try {
            rwl.readLock().lock();

            Table<String, String, Set<Address>> result = HashBasedTable.create();

            if (config != null) {
                for (Table.Cell<String, String, List<RuleGroup>> cell : config.cellSet()) {
                    Set<Address> addresses = result.get(cell.getRowKey(), cell.getColumnKey());
                    if (addresses == null) {
                        addresses = Sets.newHashSet();
                        result.put(cell.getRowKey(), cell.getColumnKey(), addresses);
                    }
                    addresses.addAll(Optional.ofNullable(cell.getValue())
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(ruleGroup -> ruleGroup.getDefaultCluster() != null)
                            .flatMap(ruleGroup -> ruleGroup.getDefaultCluster().getAddresses().stream())
                            .collect(Collectors.toSet()));
                    addresses.addAll(Optional.ofNullable(cell.getValue())
                            .orElse(Collections.emptyList())
                            .stream()
                            .flatMap(ruleGroup -> ruleGroup.getRules().stream())
                            .flatMap(rule -> rule.getCluster().getAddresses().stream())
                            .collect(Collectors.toSet()));
                }
            }

            // 代码配置级别的默认地址列表
            for (Table.Cell<String, String, List<Address>> cell : defaultConfig.cellSet()) {
                Set<Address> addresses = result.get(cell.getRowKey(), cell.getColumnKey());
                if (addresses == null) {
                    addresses = Sets.newHashSet();
                    result.put(cell.getRowKey(), cell.getColumnKey(), addresses);
                }
                addresses.addAll(Optional.ofNullable(cell.getValue())
                        .orElse(Collections.emptyList()));
            }

            return result;
        } finally {
            rwl.readLock().unlock();
        }
    }

    public static ProxyRuleConfig getInstance() {
        return RouteRuleConfigInstanceHolder.instance;
    }

    private ProxyRuleConfig() {
    }

    private static class RouteRuleConfigInstanceHolder {
        private static final ProxyRuleConfig instance = new ProxyRuleConfig();
    }
}
