package com.github.mufanh.frp.agent;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.DefaultFrpContext;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyConfig;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author xinquan.huangxq
 */
public class Main {


    public static void main(String[] args) {
        System.setProperty("frp.extension.definition.file", "D:\\TmpFiles\\paths.definition");

        ProxyConfig proxyConfig = ProxyConfig.builder()
                .appName("netpay-route-server")
                .protocol("TCP2-UTF8")
                .ip("127.0.0.1")
                .port(10086)
                .codecPluginId("frp-codec-tcp2")
                .codecType("com.github.mufanh.plugins.codec.tcp2.Tcp2UTF8Codec")
                .timeout(5000)
                .defaultAddresses(Collections.singletonList(Address.of("127.0.0.1", 9999)))
                .build();

        FrpContext frpContext = new DefaultFrpContext();
        frpContext.addProxyService(proxyConfig);
        frpContext.start();
    }
}