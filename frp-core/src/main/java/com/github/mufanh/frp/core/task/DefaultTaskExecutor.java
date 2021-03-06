package com.github.mufanh.frp.core.task;

import com.github.mufanh.frp.core.ErrCode;
import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.ExchangeProxyContext;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.ProxyException;
import com.github.mufanh.frp.core.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class DefaultTaskExecutor extends AbstractLifeCycle implements TaskExecutor {

    private final TaskConfig config;

    private ScheduledExecutorService scheduledExecutor;

    private ExecutorService synchronousExecutor;

    public DefaultTaskExecutor(final TaskConfig config) {
        this.config = config;
    }

    public DefaultTaskExecutor() {
        this.config = TaskConfig.builder().build();
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        scheduledExecutor = new ScheduledThreadPoolExecutor(
                config.getScheduledTaskExecutorPoolSize(),
                new NamedThreadFactory("TaskExecutor-scheduled"));
        synchronousExecutor = new ThreadPoolExecutor(
                config.getCoreTaskExecutorPoolSize(),
                config.getMaxTaskExecutorPoolSize(),
                config.getTaskExecutorKeepalive(),
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("TaskExecutor-synchronous"));

    }

    @Override
    public void stop() throws LifeCycleException {
        super.stop();

        scheduledExecutor.shutdown();
        synchronousExecutor.shutdown();
    }

    @Override
    public void executeImmediately(ExchangeProxyContext context, Task task) {
        ensureStarted();
        try {
            synchronousExecutor.execute(TaskRunner.make(task, context));
        } catch (RejectedExecutionException e) {
            handleRejectedException(context, e);
        }
    }

    @Override
    public void execute(ExchangeProxyContext context, Task task, int delay) {
        ensureStarted();
        try {
            scheduledExecutor.schedule(TaskRunner.make(task, context), delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            handleRejectedException(context, e);
        }
    }

    @Override
    public void execute(ExchangeProxyContext context, Task task) {
        ensureStarted();
        try {
            scheduledExecutor.execute(TaskRunner.make(task, context));
        } catch (RejectedExecutionException e) {
            handleRejectedException(context, e);
        }
    }

    private static void handleRejectedException(final ExchangeProxyContext context, RejectedExecutionException e) {
        log.error("系统超载，线程池耗尽！");
        if (context != null && context.getExceptionHandler() != null) {
            ProxyException be = new ProxyException(ErrCode.PROXY_SYSTEM_BUSY, "系统繁忙，请稍候再试。");
            context.getExceptionHandler().handleException(context, be);
        }
    }
}
