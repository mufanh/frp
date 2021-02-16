package com.github.mufanh.frp.core.extension;

import com.github.mufanh.frp.common.extension.PreconditionFactory;
import com.github.mufanh.frp.common.extension.Protocol;
import com.github.mufanh.frp.common.extension.LoadBalance;

/**
 * @author xinquan.huangxq
 */
public interface ExtensionManager {

    /**
     * 获取编码解码器
     *
     * @param type
     * @param pluginId
     * @return
     */
    Protocol protocol(String type, String pluginId);

    /**
     * 获取LoadBalance
     *
     * @param type
     * @param pluginId
     * @return
     */
    LoadBalance loadBalance(String type, String pluginId);

    /**
     * 获取规则条件工厂
     *
     * @param type
     * @param pluginId
     * @return
     */
    PreconditionFactory preconditionFactory(String type, String pluginId);

    interface Aware {

        void setExtensionManager(ExtensionManager extensionManager);

        ExtensionManager getExtensionManager();
    }
}
