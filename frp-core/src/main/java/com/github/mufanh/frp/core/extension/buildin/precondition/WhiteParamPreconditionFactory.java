package com.github.mufanh.frp.core.extension.buildin.precondition;

import com.github.mufanh.frp.common.extension.Precondition;
import com.github.mufanh.frp.common.extension.PreconditionFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xinquan.huangxq
 */
public class WhiteParamPreconditionFactory implements PreconditionFactory {

    @Override
    public Precondition create(String[] args) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("参数错误");
        }
        String[] array = StringUtils.split(args[1], ",");
        return new WhiteParamPrecondition(args[0], Stream.of(array).collect(Collectors.toSet()));
    }
}
