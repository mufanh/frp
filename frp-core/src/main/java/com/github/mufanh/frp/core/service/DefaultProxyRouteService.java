package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.Cluster;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.RouteRuleConfig;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public class DefaultProxyRouteService extends AbstractLifeCycle implements ProxyRouteService {

    private static final RouteRuleConfig config = RouteRuleConfig.getInstance();

    private final FrpContext frpContext;

    private ChannelChooseService channelChooseService;

    private ProxySelectService proxySelectService;

    public DefaultProxyRouteService(FrpContext frpContext) {
        this.frpContext = frpContext;
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        channelChooseService = frpContext.getChannelChooseService();
        proxySelectService = frpContext.getProxySelectService();
    }

    @Override
    public RouteResult route(ProxyContext context) {
        ensureStarted();

        SelectResult selectResult = proxySelectService.select(context);
        if (!selectResult.isSuccess()) {
            return defaultRoute(context);
        }

        // 按照分流规则优先级从高到低依次执行，找到满足的集群提供服务
        List<Cluster> clusters = selectResult.getClusters();
        if (CollectionUtils.isEmpty(clusters)) {
            return defaultRoute(context);
        }

        // 找到有效的服务地址列表
        for (Cluster cluster : clusters) {
            ChooseResult chooseResult = channelChooseService.choose(context, cluster);
            if (chooseResult.isSuccess()) {
                return RouteResult.success(chooseResult.getAddress());
            }
        }

        return defaultRoute(context);
    }

    /**
     * 根据路由规则找不到合适的，那么会去走容灾配置的服务地址列表
     *
     * @param context
     * @return
     */
    private RouteResult defaultRoute(ProxyContext context) {
        List<Address> addresses = config.getDefaultConfig(context.getAppName(), context.getProtocol());
        if (CollectionUtils.isEmpty(addresses)) {
            return RouteResult.error("未找到能够提供服务的地址列表");
        }
        ChooseResult chooseResult = channelChooseService.choose(context, addresses);
        if (chooseResult.isSuccess()) {
            return RouteResult.success(chooseResult.getAddress());
        }
        return RouteResult.error("未找到能够提供服务的地址列表");
    }
}
