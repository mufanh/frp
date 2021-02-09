package com.github.mufanh.frp.common;

import com.google.common.base.Preconditions;
import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class Address {

    private final String ip;

    private final int port;

    private Address(String ip, int port) {
        Preconditions.checkArgument(port >= 1 && port <= 65535,
                "端口范围1~65535");

        this.ip = Preconditions.checkNotNull(ip,
                "机器ip不能为空");
        this.port = port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public static Address of(String host, int port) {
        return new Address(host, port);
    }
}
