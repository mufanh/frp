package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Cluster;
import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.Rule;
import com.github.mufanh.frp.common.RuleGroup;
import com.github.mufanh.frp.core.config.RouteRuleConfig;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
public class DefaultProxySelectService implements ProxySelectService {

    private static final RouteRuleConfig config = RouteRuleConfig.getInstance();

    @Override
    public SelectResult select(ProxyContext context) {
        List<RuleGroup> ruleGroups = config.getConfig(context.getAppName(), context.getProtocol());
        if (CollectionUtils.isEmpty(ruleGroups)) {
            return SelectResult.error("分流规则列表为空");
        }
        List<Cluster> result = visitRuleGroups(ruleGroups, context);
        if (CollectionUtils.isEmpty(result)) {
            return SelectResult.error("没有找到匹配的分流规则列表");
        }
        return SelectResult.success(result);
    }

    private static List<Cluster> visitRuleGroups(List<RuleGroup> ruleGroups, ProxyContext context) {
        return ruleGroups.stream()
                .flatMap(ruleGroup -> visitRuleGroup(ruleGroup, context).stream())
                .collect(Collectors.toList());
    }

    private static List<Cluster> visitRuleGroup(RuleGroup ruleGroup, ProxyContext context) {
        List<Cluster> result = ruleGroup.getRules()
                .stream()
                .filter(rule -> rule.getCondition().check(context))
                .map(Rule::getCluster)
                .collect(Collectors.toList());
        if (ruleGroup.getDefaultCluster() != null) {
            result.add(ruleGroup.getDefaultCluster());
        }
        return result;
    }
}
