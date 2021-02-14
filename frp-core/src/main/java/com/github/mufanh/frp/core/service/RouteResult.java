package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class RouteResult {

    private final boolean success;

    private final String msg;

    private final Address address;

    private RouteResult(boolean success, String msg, Address address) {
        this.success = success;
        this.msg = msg;
        this.address = address;
    }

    public static RouteResult error(String msg) {
        return new RouteResult(false, msg, null);
    }

    public static RouteResult success(Address address) {
        return new RouteResult(true, null, address);
    }
}
