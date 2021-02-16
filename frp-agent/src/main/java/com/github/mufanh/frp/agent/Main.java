package com.github.mufanh.frp.agent;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.core.DefaultFrpContext;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.config.ProxyServerConfig;

import java.util.Collections;

/**
 * @author xinquan.huangxq
 */
public class Main {


    public static void main(String[] args) {
        System.setProperty("frp.extension.definition.file", "D:\\TmpFiles\\paths.definition");

        ProxyServerConfig proxyServerConfig = ProxyServerConfig.builder()
                .appName("netpay-route-server")
                .protocol("TCP2-UTF8")
                .ip("127.0.0.1")
                .port(10086)
                .protocolPluginId("frp-protocol-tcp2")
                .protocolType("com.github.mufanh.plugins.protocol.tcp2.Tcp2UTF8Protocol")
                .proxyInvokeTimeout(50000)
                .defaultAddresses(Collections.singletonList(Address.of("127.0.0.1", 9999)))
                .build();

        FrpContext frpContext = new DefaultFrpContext();
        frpContext.addProxyService(proxyServerConfig);
        frpContext.start();
    }
}
