package com.github.mufanh.frp.core;

/**
 * @author xinquan.huangxq
 */
public abstract class AbstractExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(ExchangeProxyContext context, Throwable t) {
        if (context == null) {
            context = new DefaultExchangeProxyContext(null);
        }
        context.setException(t);

        if (t instanceof ProxyException) {
            ProxyException pe = (ProxyException) t;
            context.setCode(pe.getErrCode().code());
            context.setMessage(t.getMessage());
        } else {
            context.setCode(ErrCode.PROXY_INNER_ERROR.code());
            context.setMessage(ErrCode.PROXY_INNER_ERROR.message());
        }

        response(context);
    }

    protected abstract void response(ExchangeProxyContext context);
}
