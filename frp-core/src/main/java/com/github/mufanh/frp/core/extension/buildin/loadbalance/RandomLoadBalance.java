package com.github.mufanh.frp.core.extension.buildin.loadbalance;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.extension.LoadBalance;
import org.apache.commons.collections4.CollectionUtils;
import org.pf4j.Extension;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xinquan.huangxq
 */
@Extension
public class RandomLoadBalance implements LoadBalance {

    @Override
    public Address balance(ProxyContext context, List<Address> addresses) {
        if (CollectionUtils.isEmpty(addresses)) {
            return null;
        }
        if (addresses.size() == 1) {
            return addresses.get(0);
        }
        return addresses.get(ThreadLocalRandom.current().nextInt(addresses.size()));
    }
}
