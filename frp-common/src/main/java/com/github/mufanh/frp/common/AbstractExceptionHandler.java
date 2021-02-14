package com.github.mufanh.frp.common;

/**
 * @author xinquan.huangxq
 */
public abstract class AbstractExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(ProxyContext context, Throwable t) {
        if (context == null) {
            context = new ProxyContext();
        }
        context.setException(t);

        if (t instanceof ProxyException) {
            ProxyException pe = (ProxyException) t;
            context.setErrCode(pe.getErrCode());
            context.setDetailErrorMsg(t.getMessage());
        } else {
            context.setErrCode(ErrCode.PROXY_INNER_ERROR);
            context.setDetailErrorMsg(ErrCode.PROXY_INNER_ERROR.message());
        }

        response(context);
    }

    protected abstract void response(ProxyContext context);
}
