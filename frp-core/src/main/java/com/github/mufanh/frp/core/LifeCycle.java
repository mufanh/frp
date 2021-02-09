package com.github.mufanh.frp.core;

/**
 * @author xinquan.huangxq
 */
public interface LifeCycle {

    void start() throws LifeCycleException;

    void stop() throws LifeCycleException;

    boolean isStarted();
}
