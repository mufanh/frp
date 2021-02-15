package com.github.mufanh.frp.common.extension;

import org.pf4j.ExtensionPoint;

/**
 * @author xinquan.huangxq
 */
public interface PreconditionFactory extends ExtensionPoint {

    Precondition create(String[] args);
}
