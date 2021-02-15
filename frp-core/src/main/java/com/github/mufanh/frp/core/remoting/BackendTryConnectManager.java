package com.github.mufanh.frp.core.remoting;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.LifeCycle;

/**
 * @author xinquan.huangxq
 */
public interface BackendTryConnectManager extends LifeCycle {

    void tryConnect(String appName, String protocol, Address address);

    void publishConfigThenTryConnectAll();

    interface Aware {
        void setBackendTryConnectManager(BackendTryConnectManager backendTryConnectManager);

        BackendTryConnectManager getBackendTryConnectManager();
    }
}
