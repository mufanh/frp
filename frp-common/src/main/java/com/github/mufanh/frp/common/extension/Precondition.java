package com.github.mufanh.frp.common.extension;

/**
 * @author xinquan.huangxq
 */
public interface Precondition {

    Precondition NULL = exchange -> true;

    /**
     * 判断是否符合分流条件
     *
     * @param context
     * @return
     */
    boolean check(ProxyContext context);
}
