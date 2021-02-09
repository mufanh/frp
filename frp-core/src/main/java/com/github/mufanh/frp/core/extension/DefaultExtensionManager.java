package com.github.mufanh.frp.core.extension;

import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.SystemConfigs;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.DefaultExtensionFinder;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFinder;
import org.pf4j.PluginManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
public class DefaultExtensionManager extends AbstractLifeCycle implements ExtensionManager {

    private final PluginManager pluginManager;

    private final List<Path> pluginPaths;

    public DefaultExtensionManager() {
        this.pluginManager = new DefaultPluginManager() {
            @Override
            protected ExtensionFinder createExtensionFinder() {
                DefaultExtensionFinder extensionFinder = (DefaultExtensionFinder) super.createExtensionFinder();
                extensionFinder.addServiceProviderExtensionFinder();
                return extensionFinder;
            }
        };
        this.pluginPaths = preparePluginPaths();
    }

    @Override
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

    @Override
    public void stop() throws LifeCycleException {
        super.stop();

        pluginManager.stopPlugins();
    }

    private List<Path> preparePluginPaths() {
        String extensionDefinitionFile = SystemConfigs.EXTENSION_DEFINITION_FILE.getString();
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
}
