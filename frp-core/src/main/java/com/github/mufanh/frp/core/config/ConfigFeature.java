package com.github.mufanh.frp.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author xinquan.huangxq
 */
public class ConfigFeature {

    private Map<String, Object> features;

    public ConfigFeature addFeature(String key, Object value) {
        if (features == null) {
            features = new HashMap<>(4);
        }
        features.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getFeature(String key) {
        return (T) features.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFeature(String key, Supplier<T> supplier) {
        T result = (T) features.get(key);
        if (result == null) {
            result = supplier.get();
        }
        return result;
    }
}
