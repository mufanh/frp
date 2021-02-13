package com.github.mufanh.frp.core.task;

import com.github.mufanh.frp.common.ProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface TaskExecutor {

    /**
     * 立即执行任务，如果线程池满，直接拒绝执行
     *
     * @param context
     * @param task
     */
    void executeImmediately(final ProxyContext context, final Task task);

    /**
     * 延迟执行
     *
     * @param context
     * @param task
     * @param delay
     */
    void execute(final ProxyContext context, final Task task, int delay);

    /**
     * 立即执行任务，使用无界队列
     *
     * @param context
     * @param task
     */
    void execute(final ProxyContext context, final Task task);
}
