package com.github.mufanh.frp.core.extension;

import com.github.mufanh.frp.common.extension.PreconditionFactory;
import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.LoadBalance;
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
public class DefaultExtensionManager implements ExtensionManager {

    private final Map<String/*extensionClass@pluginId*/, List<?>> extensionsCache = Maps.newConcurrentMap();

    private final PluginManager pluginManager;

    private final List<Path> pluginPaths;

    public DefaultExtensionManager() {
        this.pluginManager = new DefaultPluginManager();
        this.pluginPaths = preparePluginPaths();

        // 启动插件管理器
        loadThenStartPlugins();
    }

    @Override
    public Protocol protocol(String type, String pluginId) {
        return getExtension(type, pluginId, Protocol.class);
    }

    @Override
    public LoadBalance loadBalance(String type, String pluginId) {
        return getExtension(type, pluginId, LoadBalance.class);
    }

    @Override
    public PreconditionFactory preconditionFactory(String type, String pluginId) {
        return getExtension(type, pluginId, PreconditionFactory.class);
    }

    private void loadThenStartPlugins() {
        // 加载插件
        if (CollectionUtils.isNotEmpty(pluginPaths)) {
            pluginPaths.forEach(pluginManager::loadPlugin);
        } else {
            pluginManager.loadPlugins();
        }

        // 启动插件
        pluginManager.startPlugins();
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

    protected <T extends ExtensionPoint> T getExtension(String type, String pluginId, Class<T> typeClass) {
        List<T> extensions = getExtensions(typeClass, pluginId);
        for (T extension : extensions) {
            if (StringUtils.equals(extension.getClass().getName(), type)) {
                return extension;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends ExtensionPoint> List<T> getExtensions(Class<T> typeClass, String pluginId) {
        String key = extensionsCacheKey(typeClass, pluginId);
        List<?> cachedExtensions = extensionsCache.get(key);
        if (cachedExtensions != null) {
            return (List<T>) cachedExtensions;
        }

        List<T> extensions = pluginManager.getExtensions(typeClass, pluginId);
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
