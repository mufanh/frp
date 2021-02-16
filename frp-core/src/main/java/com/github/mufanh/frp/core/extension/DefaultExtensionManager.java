package com.github.mufanh.frp.core.extension;

import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.LoadBalance;
import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
public class DefaultExtensionManager extends AbstractLifeCycle implements ExtensionManager {

    private final Map<String/*extensionClass@pluginId*/, List<?>> extensionsCache = Maps.newConcurrentMap();

    private final PluginManager pluginManager;

    private final List<Path> pluginPaths;

    public DefaultExtensionManager() {
        this.pluginManager = new DefaultPluginManager();
        this.pluginPaths = preparePluginPaths();

        // 初始化后直接启动
        start();
    }

    public void start() throws LifeCycleException {
        super.start();

        // 加载插件
        if (CollectionUtils.isNotEmpty(pluginPaths)) {
            pluginPaths.forEach(pluginManager::loadPlugin);
        } else {
            pluginManager.loadPlugins();
        }

        // 启动插件
        pluginManager.startPlugins();
    }

    public void stop() throws LifeCycleException {
        super.stop();

        pluginManager.stopPlugins();
    }

    private List<Path> preparePluginPaths() {
        String extensionDefinitionFile = SystemConfigs.EXTENSION_DEFINITION_FILE.stringValue();
        if (StringUtils.isBlank(extensionDefinitionFile)) {
            return Collections.emptyList();
        }

        File file = new File(extensionDefinitionFile);
        if (file.isDirectory() || !file.exists()) {
            throw new IllegalArgumentException("扩展插件定义文件路径不存在或者配置的路径为目录，无法加载扩展插件定义描述文件，加载扩展插件失败.");
        }

        try {
            return Files.lines(Paths.get(extensionDefinitionFile))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .map(path -> Paths.get(path))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("读取扩展插件定义文件失败，加载扩展插件失败.", e);
        }
    }

    @Override
    public Protocol protocol(String type, String pluginId) {
        List<Protocol> protocols = getExtensions(Protocol.class, pluginId);
        for (Protocol protocol : protocols) {
            if (StringUtils.equals(protocol.getClass().getName(), type)) {
                return protocol;
            }
        }
        return null;
    }

    @Override
    public LoadBalance loadBalance(String type, String pluginId) {
        List<LoadBalance> loadBalances = getExtensions(LoadBalance.class, pluginId);
        for (LoadBalance loadBalance : loadBalances) {
            if (StringUtils.equals(loadBalance.getClass().getName(), type)) {
                return loadBalance;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExtensionPoint> List<T> getExtensions(Class<T> type, String pluginId) {
        ensureStarted();

        String key = extensionsCacheKey(type, pluginId);
        List<?> cachedExtensions = extensionsCache.get(key);
        if (cachedExtensions != null) {
            return (List<T>) cachedExtensions;
        }

        List<T> extensions = pluginManager.getExtensions(type, pluginId);
        List<?> lastExtensions = extensionsCache.putIfAbsent(key, extensions);
        if (lastExtensions == null) {
            return extensions;
        } else {
            return (List<T>) lastExtensions;
        }
    }

    private static String extensionsCacheKey(Class<?> type, String pluginId) {
        return type.getName() + "@" + pluginId;
    }
}
