package com.github.mufanh.frp.core.task;

import com.github.mufanh.frp.core.ExchangeProxyContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class TaskRunner implements Runnable {

    private final Task task;

    private final ExchangeProxyContext context;

    private TaskRunner(Task task, ExchangeProxyContext context) {
        this.task = task;
        this.context = context;
    }

    public static TaskRunner make(Task task, ExchangeProxyContext context) {
        return new TaskRunner(task, context);
    }

    @Override
    public void run() {
        try {
            task.run();
        } catch (Throwable e) {
            if (context != null && context.getExceptionHandler() != null) {
                context.getExceptionHandler().handleException(context, e);
            } else {
                log.error("未捕获异常：", e);
            }
        }
    }
}
