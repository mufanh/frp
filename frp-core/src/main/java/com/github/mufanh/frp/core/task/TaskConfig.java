package com.github.mufanh.frp.core.task;

import com.github.mufanh.frp.core.config.SystemConfigs;
import lombok.Getter;

/**
 * @author xinquan.huangxq
 */
@Getter
public class TaskConfig {

    private final int scheduledTaskExecutorPoolSize;

    private final int coreTaskExecutorPoolSize;

    private final int maxTaskExecutorPoolSize;

    private final long taskExecutorKeepalive;

    public TaskConfig(int scheduledTaskExecutorPoolSize,
                      int coreTaskExecutorPoolSize,
                      int maxTaskExecutorPoolSize,
                      long taskExecutorKeepalive) {
        this.scheduledTaskExecutorPoolSize = scheduledTaskExecutorPoolSize;
        this.coreTaskExecutorPoolSize = coreTaskExecutorPoolSize;
        this.maxTaskExecutorPoolSize = maxTaskExecutorPoolSize;
        this.taskExecutorKeepalive = taskExecutorKeepalive;
    }

    public static class Builder {

        private Integer scheduledTaskExecutorPoolSize;

        private Integer coreTaskExecutorPoolSize;

        private Integer maxTaskExecutorPoolSize;

        private Long taskExecutorKeepalive;

        public Builder scheduledTaskExecutorPoolSize(int scheduledTaskExecutorPoolSize) {
            this.scheduledTaskExecutorPoolSize = scheduledTaskExecutorPoolSize;
            return this;
        }

        public Builder coreTaskExecutorPoolSize(int coreTaskExecutorPoolSize) {
            this.coreTaskExecutorPoolSize = coreTaskExecutorPoolSize;
            return this;
        }

        public Builder maxTaskExecutorPoolSize(int maxTaskExecutorPoolSize) {
            this.maxTaskExecutorPoolSize = maxTaskExecutorPoolSize;
            return this;
        }

        public Builder taskExecutorKeepalive(long taskExecutorKeepalive) {
            this.taskExecutorKeepalive = taskExecutorKeepalive;
            return this;
        }

        public TaskConfig build() {
            if (scheduledTaskExecutorPoolSize == null) {
                scheduledTaskExecutorPoolSize = SystemConfigs.TASK_EXECUTOR_SCHEDULED_POOL_SIZE.intValue();
            }
            if (coreTaskExecutorPoolSize == null) {
                coreTaskExecutorPoolSize = SystemConfigs.TASK_EXECUTOR_CORE_POOL_SIZE.intValue();
            }
            if (maxTaskExecutorPoolSize == null) {
                maxTaskExecutorPoolSize = SystemConfigs.TASK_EXECUTOR_MAX_POOL_SIZE.intValue();
            }
            if (taskExecutorKeepalive == null) {
                taskExecutorKeepalive = SystemConfigs.TASK_EXECUTOR_KEEP_ALIVE.longValue();
            }

            return new TaskConfig(scheduledTaskExecutorPoolSize, coreTaskExecutorPoolSize, maxTaskExecutorPoolSize, taskExecutorKeepalive);
        }
    }
}
