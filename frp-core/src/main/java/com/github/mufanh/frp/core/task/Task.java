package com.github.mufanh.frp.core.task;

import com.github.mufanh.frp.common.ProxyException;

/**
 * @author xinquan.huangxq
 */
public interface Task {

    void run() throws ProxyException;
}
