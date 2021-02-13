package com.github.mufanh.frp.core.config;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xinquan.huangxq
 */
public enum SystemConfigs {
    /**
     * 扩展插件定义文件路径
     */
    EXTENSION_DEFINITION_FILE("frp.extension.definition.file", "./extension.definition"),

    /**
     * 线程池默认值
     */
    TASK_EXECUTOR_SCHEDULED_POOL_SIZE("frp.executor.scheduled.pool.size", 64),
    TASK_EXECUTOR_CORE_POOL_SIZE("frp.executor.core.pool.size", 8),
    TASK_EXECUTOR_MAX_POOL_SIZE("frp.executor.max.pool.size", 1024),
    TASK_EXECUTOR_KEEP_ALIVE("frp.executor.keepalive", 600L),
    ;

    @Getter
    private final String propertyKey;

    @Getter
    private final Object defaultValue;

    SystemConfigs(String propertyKey, Object defaultValue) {
        this.propertyKey = Preconditions.checkNotNull(propertyKey);
        this.defaultValue = defaultValue;
    }

    public boolean boolValue() {
        Preconditions.checkArgument(defaultValue instanceof Boolean);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (boolean) defaultValue
                : Boolean.parseBoolean(propertyValue);
    }

    public Boolean getBool() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Boolean);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (Boolean) defaultValue
                : Boolean.parseBoolean(propertyValue);
    }

    public int intValue() {
        Preconditions.checkArgument(defaultValue instanceof Integer);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (int) defaultValue
                : Integer.parseInt(propertyValue);
    }

    public Integer getInt() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Integer);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (Integer) defaultValue
                : Integer.parseInt(propertyValue);
    }

    public long longValue() {
        Preconditions.checkArgument(defaultValue instanceof Long);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (long) defaultValue
                : Long.parseLong(propertyValue);
    }

    public Long getLong() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Long);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (Long) defaultValue
                : Long.parseLong(propertyValue);
    }

    public String stringValue() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof String);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (String) defaultValue
                : propertyValue;
    }

    private String systemPropertyValue() {
        return System.getProperty(propertyKey);
    }
}
