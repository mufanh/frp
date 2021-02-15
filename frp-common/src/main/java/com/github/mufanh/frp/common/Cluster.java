package com.github.mufanh.frp.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
@Data
public class Cluster {

    private final String clusterId;

    private final String protocol;

    private final String appName;

    private final List<Address> addresses;

    private Cluster(String clusterId, String appName, String protocol, Set<Address> addresses) {
        this.clusterId = clusterId;
        this.appName = appName;
        this.protocol = protocol;
        this.addresses = ImmutableList.copyOf(addresses);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String clusterId;

        private String appName;

        private String protocol;

        private Set<Address> addresses;

        public Builder clusterId(String clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder addAddress(Address address) {
            if (addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            return this;
        }

        public Builder addAddresses(List<Address> addresses) {
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(addresses));

            for (Address address : addresses) {
                addAddress(address);
            }
            return this;
        }

        public Builder addAddress(String host, int port) {
            return addAddress(Address.of(host, port));
        }

        public Cluster build() {
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterId),
                    "请设置集群标识");
            Preconditions.checkArgument(StringUtils.isNotBlank(appName),
                    "请设置集群服务于哪个应用");
            Preconditions.checkArgument(StringUtils.isNotBlank(protocol),
                    "请设置集群服务于应用的哪个协议服务");
            Preconditions.checkArgument(CollectionUtils.isNotEmpty(addresses),
                    "集群地址列表不能为空");

            return new Cluster(clusterId, appName, protocol, addresses);
        }
    }
}
