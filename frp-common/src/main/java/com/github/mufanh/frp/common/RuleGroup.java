package com.github.mufanh.frp.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xinquan.huangxq
 */
@Data
public class RuleGroup {

    private final String appName;

    private final String protocol;

    private final List<Rule> rules;

    private final Cluster defaultCluster;

    private RuleGroup(String appName, String protocol, List<Rule> rules, Cluster defaultCluster) {
        this.appName = appName;
        this.protocol = protocol;
        this.rules = ImmutableList.copyOf(rules);
        this.defaultCluster = defaultCluster;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String appName;

        private String protocol;

        private List<Rule> rules;

        private Cluster defaultCluster;

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder addRule(Rule rule) {
            if (rules == null) {
                rules = new ArrayList<>();
            }
            rules.add(rule);
            return this;
        }

        public Builder addRules(List<Rule> rules) {
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(rules));

            for (Rule rule : rules) {
                addRule(rule);
            }
            return this;
        }

        public Builder defaultCluster(Cluster defaultCluster) {
            this.defaultCluster = defaultCluster;
            return this;
        }

        public RuleGroup build() {
            Preconditions.checkArgument(StringUtils.isNotBlank(appName),
                    "请设置规则分组服务于哪个应用");
            Preconditions.checkArgument(StringUtils.isNotBlank(protocol),
                    "请设置规则分组服务于应用的哪个协议服务");
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(rules) || defaultCluster != null,
                    "规则分组的规则列表和默认集群不能同时为空");

            if (rules == null) {
                rules = Collections.emptyList();
            }

            return new RuleGroup(appName, protocol, rules, defaultCluster);
        }
    }
}
