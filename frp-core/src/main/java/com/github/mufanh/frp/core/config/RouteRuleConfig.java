package com.github.mufanh.frp.core.config;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.RuleGroup;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class RouteRuleConfig {

    // 整体进行替换，运行过程中会重新构造
    private final AtomicReference<Table<String/*appName*/, String/*protocol*/, List<RuleGroup>>> configReference
            = new AtomicReference<>();

    // 分批进行替换
    // 仅启动时候会加载初始化一次
    private final Table<String/*appName*/, String/*protocol*/, List<Address>> defaultConfig = HashBasedTable.create();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();


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
        if (StringUtils.isBlank(appName) || StringUtils.isNotBlank(protocol)) {
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
        Table<String, String, List<RuleGroup>> config = configReference.get();
        if (config == null) {
            return Collections.emptyList();
        }
        List<RuleGroup> result = config.get(appName, protocol);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public void setConfig(List<RuleGroup> ruleGroups) {
        if (CollectionUtils.isEmpty(ruleGroups)) {
            configReference.set(null);
            return;
        }
        Table<String, String, List<RuleGroup>> config = HashBasedTable.create();
        for (RuleGroup ruleGroup : ruleGroups) {
            List<RuleGroup> groups = config.get(ruleGroup.getAppName(), ruleGroup.getProtocol());
            if (groups == null) {
                groups = new ArrayList<>();
                config.put(ruleGroup.getAppName(), ruleGroup.getProtocol(), groups);
            }
            groups.add(ruleGroup);
        }
        configReference.set(config);
    }

    public static RouteRuleConfig getInstance() {
        return RouteRuleConfigInstanceHolder.instance;
    }

    private RouteRuleConfig() {
    }

    private static class RouteRuleConfigInstanceHolder {
        private static final RouteRuleConfig instance = new RouteRuleConfig();
    }
}
