package com.github.mufanh.frp.core.service;

import com.github.mufanh.frp.common.Cluster;
import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xinquan.huangxq
 */
@Data
public class SelectResult {

    private final boolean success;

    private final String msg;

    private final List<Cluster> clusters;

    private SelectResult(boolean success, String msg, List<Cluster> clusters) {
        this.success = success;
        this.msg = msg;
        this.clusters = clusters;
    }

    public static SelectResult error(String msg) {
        return new SelectResult(false, msg, ImmutableList.of());
    }

    public static SelectResult success(List<Cluster> clusters) {
        return new SelectResult(true, null, clusters.stream()
                .distinct()
                .collect(Collectors.toList()));
    }
}
