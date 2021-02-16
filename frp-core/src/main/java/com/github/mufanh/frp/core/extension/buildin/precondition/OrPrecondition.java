package com.github.mufanh.frp.core.extension.buildin.precondition;

import com.github.mufanh.frp.common.extension.Precondition;
import com.github.mufanh.frp.common.extension.ProxyContext;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author xinquan.huangxq
 */
public class OrPrecondition implements Precondition {

    protected final List<Precondition> conditions;

    @Override
    public boolean check(ProxyContext context) {
        return conditions.stream().anyMatch(condition -> condition.check(context));
    }

    public OrPrecondition(Precondition left, Precondition right) {
        this.conditions = Lists.newArrayList(checkNotNull(left), checkNotNull(right));
    }

    public OrPrecondition(List<Precondition> conditions) {
        checkArgument(CollectionUtils.isNotEmpty(conditions));

        this.conditions = conditions;
    }

    public OrPrecondition(Precondition... conditions) {
        checkArgument(conditions != null && conditions.length > 0);

        this.conditions = Lists.newArrayList(conditions);
    }
}
