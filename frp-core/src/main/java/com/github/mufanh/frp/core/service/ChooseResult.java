package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class ChooseResult {

    private final boolean success;

    private final String msg;

    private final Address address;

    private ChooseResult(boolean success, String msg, Address address) {
        this.success = success;
        this.msg = msg;
        this.address = address;
    }

    public static ChooseResult error(String msg) {
        return new ChooseResult(false, msg, null);
    }

    public static ChooseResult success(Address address) {
        return new ChooseResult(true, null, address);
    }
}
