package com.github.mufanh.frp.core.extension.buildin.precondition;

import com.github.mufanh.frp.common.extension.Precondition;
import com.github.mufanh.frp.common.extension.ProxyContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author xinquan.huangxq
 */
public class NotPrecondition implements Precondition {

    protected final Precondition condition;

    public NotPrecondition(Precondition condition) {
        this.condition = checkNotNull(condition);
    }

    @Override
    public boolean check(ProxyContext context) {
        return !condition.check(context);
    }
}
