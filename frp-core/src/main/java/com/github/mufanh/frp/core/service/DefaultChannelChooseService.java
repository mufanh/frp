package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Address;
import com.github.mufanh.frp.common.Cluster;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.extension.LoadBalance;
import com.github.mufanh.frp.core.AbstractLifeCycle;
import com.github.mufanh.frp.core.FrpContext;
import com.github.mufanh.frp.core.LifeCycleException;
import com.github.mufanh.frp.core.config.SystemConfigs;
import com.github.mufanh.frp.core.extension.ExtensionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class DefaultChannelChooseService extends AbstractLifeCycle implements ChannelChooseService {

    private final FrpContext frpContext;

    private ChannelHealthCheck channelHealthCheck;

    private ExtensionManager extensionManager;

    public DefaultChannelChooseService(FrpContext frpContext) {
        this.frpContext = frpContext;
    }

    @Override
    public void start() throws LifeCycleException {
        super.start();

        channelHealthCheck = frpContext.getChannelHealthCheck();
        extensionManager = frpContext.getExtensionManager();
    }

    @Override
    public ChooseResult choose(ProxyContext context, Cluster cluster) {
        return choose(context, cluster.getAddresses());
    }

    @Override
    public ChooseResult choose(ProxyContext context, List<Address> addresses) {
        ensureStarted();

        if (CollectionUtils.isEmpty(addresses)) {
            return ChooseResult.error("没有合适的地址列表，无法选择合适目标服务地址");
        }

        List<Address> checkedAddresses;
        if (channelHealthCheck == null) {
            // 没有配置，则认为都可以
            checkedAddresses = addresses;
        } else {
            checkedAddresses = addresses.stream()
                    .filter(address -> channelHealthCheck.check(context, address))
                    .collect(Collectors.toList());
        }

        LoadBalance loadBalance = prepareLoadBalance(context);
        if (loadBalance == null) {
            return ChooseResult.error("没有合适的负载组件，无法选择合适目标服务地址");
        }

        Address address = loadBalance.balance(context, checkedAddresses);
        if (address == null) {
            return ChooseResult.error("负载组件有问题，经过负载后，没有找到合适的目标服务地址");
        }

        return ChooseResult.success(address);
    }

    private LoadBalance prepareLoadBalance(ProxyContext context) {
        String type = context.getHeader(ProxyContext.HeaderKeys.LOAD_BALANCE);
        if (StringUtils.isBlank(type)) {
            type = SystemConfigs.DEFAULT_LOAD_BALANCE.stringValue();
        }
        String[] args = StringUtils.split(type, "@");
        if (args == null || args.length < 1 || args.length > 2) {
            log.info("负载组件有问题，使用默认负载组件");
            args = new String[]{SystemConfigs.DEFAULT_LOAD_BALANCE.stringValue()};
        }
        LoadBalance loadBalance;
        if (args.length == 1) {
            loadBalance = extensionManager.loadBalance(args[0], null);
        } else {
            // == 2
            loadBalance = extensionManager.loadBalance(args[0], args[1]);
        }
        return loadBalance;
    }
}
