package com.github.mufanh.frp.core.extension;

import com.github.mufanh.frp.common.extension.Codec;
import com.github.mufanh.frp.common.extension.LoadBalance;
import com.github.mufanh.frp.core.LifeCycle;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public interface ExtensionManager extends LifeCycle {

    /**
     * 获取编码解码器
     *
     * @param type
     * @param pluginId
     * @return
     */
    Codec codec(String type, String pluginId);

    /**
     * 获取LoadBalance
     *
     * @param type
     * @param pluginId
     * @return
     */
    LoadBalance loadBalance(String type, String pluginId);

    /**
     * 获取扩展插件
     *
     * @param type
     * @param <T>
     * @return
     */
    <T extends ExtensionPoint> List<T> getExtensions(Class<T> type);

    /**
     * 获取扩展插件
     *
     * @param type
     * @param pluginId
     * @param <T>
     * @return
     */
    <T extends ExtensionPoint> List<T> getExtensions(Class<T> type, String pluginId);

    interface Aware {

        void setExtensionManager(ExtensionManager extensionManager);

        ExtensionManager getExtensionManager();
    }
}
