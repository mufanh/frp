package com.github.mufanh.plugins.protocol.tcp2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author xinquan.huangxq
 */
public final class JSONUtil {

    private JSONUtil() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String map2Json(Map<String, Object> map) throws JsonProcessingException {
        if (map == null) {
            throw new IllegalArgumentException("The map is null.");
        }
        return mapper.writeValueAsString(map);
    }

    public static Map<String, Object> json2Map(String json) throws JsonProcessingException {
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException("The json is null.");
        }
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }


    public static <T> T json2Object(String json, Class<T> valueType) throws JsonProcessingException {
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException("The json is null.");
        }

        if (valueType == null) {
            throw new IllegalArgumentException("The value type is null.");
        }

        return mapper.readValue(json, valueType);
    }
}
