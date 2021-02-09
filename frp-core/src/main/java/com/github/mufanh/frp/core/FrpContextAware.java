package com.github.mufanh.frp.core;

/**
 * @author xinquan.huangxq
 */
public interface FrpContextAware {

    void setFrpContext(FrpContext frpContext);

    FrpContext getFrpContext();

    static <T> T trySetFrpContext(T object, FrpContext frpContext) {
        if (object instanceof FrpContextAware) {
            ((FrpContextAware) object).setFrpContext(frpContext);
        }
        return object;
    }
}
