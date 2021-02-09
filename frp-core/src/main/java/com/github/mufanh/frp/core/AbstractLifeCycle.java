package com.github.mufanh.frp.core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xinquan.huangxq
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void start() throws LifeCycleException {
        if (isStarted.compareAndSet(false, true)) {
            return;
        }
        throw new LifeCycleException("该组件已经启动.");
    }

    @Override
    public void stop() throws LifeCycleException {
        if (isStarted.compareAndSet(true, false)) {
            return;
        }
        throw new LifeCycleException("该组件已经停止.");
    }

    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    protected void ensureStarted() {
        if (!isStarted()) {
            throw new LifeCycleException(String.format("组件(%s)未启动，请启动后再操作!", getClass().getSimpleName()));
        }
    }
}
