package com.github.mufanh.frp.core.task;

/**
 * @author xinquan.huangxq
 */
public interface TaskExecutorAware {

    void setTaskExecutor(TaskExecutor taskExecutor);

    TaskExecutor getTaskExecutor();
}
