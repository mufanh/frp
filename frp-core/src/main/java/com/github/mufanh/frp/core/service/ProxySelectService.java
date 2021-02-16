package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.core.ExchangeProxyContext;

/**
 * @author xinquan.huangxq
 */
public interface ProxySelectService {

    /**
     * 根据路由规则优先级，从高到底执行，将命中的规则从高到低一次添加到结果列表中
     *
     * @param context
     * @return
     */
    SelectResult select(ExchangeProxyContext context);

    interface Aware {

        void setProxySelectService(ProxySelectService proxySelectService);

        ProxySelectService getProxySelectService();
    }
}
