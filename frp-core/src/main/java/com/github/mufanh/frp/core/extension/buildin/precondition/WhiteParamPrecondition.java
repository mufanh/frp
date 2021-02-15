package com.github.mufanh.frp.core.extension.buildin.precondition;

import com.github.mufanh.frp.common.ProxyContext;
import com.github.mufanh.frp.common.extension.Precondition;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
public class WhiteParamPrecondition implements Precondition {

    private final String paramKey;

    private final Set<String> whitelist;

    public WhiteParamPrecondition(String paramKey, Set<String> whitelist) {
        this.paramKey = paramKey;
        this.whitelist = whitelist;
    }

    public static WhiteParamPrecondition of(String paramKey, Collection<String> whitelist) {
        return new WhiteParamPrecondition(paramKey, Sets.newHashSet(whitelist));
    }

    public static WhiteParamPrecondition of(String paramKey, String... whitelist) {
        return new WhiteParamPrecondition(paramKey, Sets.newHashSet(whitelist));
    }

    @Override
    public boolean check(ProxyContext context) {
        if (context == null) {
            return false;
        }
        String paramValue = context.getParam(paramKey);
        return whitelist.contains(paramValue);
    }
}
