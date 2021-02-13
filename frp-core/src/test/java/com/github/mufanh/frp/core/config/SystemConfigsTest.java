package com.github.mufanh.frp.core.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xinquan.huangxq
 */
public class SystemConfigsTest {

    @Test
    public void getStringDefault() {
        System.setProperty("frp.extension.definition.file", "");

        assertEquals(SystemConfigs.EXTENSION_DEFINITION_FILE.stringValue(), "./extension/paths.definition");
    }

    @Test
    public void getStringSet() {
        System.setProperty("frp.extension.definition.file", "/home/mufanh/extension/paths.definition");

        assertEquals("/home/mufanh/extension/paths.definition", SystemConfigs.EXTENSION_DEFINITION_FILE.stringValue());
    }
}