package com.github.mufanh.frp.common.extension;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.ProxyContext;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public interface LoadBalance extends ExtensionPoint {

    Address balance(ProxyContext context, List<Address> addresses);
}
