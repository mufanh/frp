package com.github.mufanh.frp.common;

import com.github.mufanh.frp.common.extension.Precondition;
import com.google.common.base.Preconditions;
import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class Rule {

    private final Precondition condition;

    private final Cluster cluster;

    private Rule(Precondition condition, Cluster cluster) {
        this.condition = condition;
        this.cluster = cluster;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Precondition condition;

        private Cluster cluster;

        public Builder condition(Precondition condition) {
            this.condition = condition;
            return this;
        }

        public Builder cluster(Cluster cluster) {
            this.cluster = cluster;
            return this;
        }

        public Rule build() {
            Preconditions.checkArgument(condition != null,
                    "设置触发规则的条件");
            Preconditions.checkArgument(cluster != null,
                    "设置触发规则命中的集群");

            return new Rule(condition, cluster);
        }
    }
}
