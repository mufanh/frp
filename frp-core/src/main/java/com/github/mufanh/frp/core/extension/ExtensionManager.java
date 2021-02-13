package com.github.mufanh.frp.core.extension;

import com.github.mufanh.frp.core.LifeCycle;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public interface ExtensionManager extends LifeCycle {

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
}
