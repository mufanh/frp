package com.github.mufanh.plugins.protocol.tcp2;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class Tcp2Plugin extends Plugin {

    public Tcp2Plugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Tcp2Plugin.start()");
    }

    @Override
    public void stop() {
        log.info("Tcp2Plugin.stop()");
    }
}
