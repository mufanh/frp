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
    EXTENSION_DEFINITION_FILE("frp.extension.definition.file", "./extension/paths.definition"),

    ;

    @Getter
    private final String propertyKey;

    @Getter
    private final Object defaultValue;

    SystemConfigs(String propertyKey, Object defaultValue) {
        this.propertyKey = Preconditions.checkNotNull(propertyKey);
        this.defaultValue = defaultValue;
    }

    public boolean getBool() {
        Preconditions.checkArgument(defaultValue instanceof Boolean);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (boolean) defaultValue
                : Boolean.parseBoolean(propertyValue);
    }

    public Boolean getBoolean() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Boolean);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (Boolean) defaultValue
                : Boolean.parseBoolean(propertyValue);
    }

    public int getInt() {
        Preconditions.checkArgument(defaultValue instanceof Integer);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (int) defaultValue
                : Integer.parseInt(propertyValue);
    }

    public Integer getInteger() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Integer);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (Integer) defaultValue
                : Integer.parseInt(propertyValue);
    }

    public String getString() {
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
