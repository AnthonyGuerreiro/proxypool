package com.ag.proxy.pool;

@SuppressWarnings("unused")
public class ProxyPoolException extends RuntimeException {

    public ProxyPoolException() {
    }

    public ProxyPoolException(final String message) {
        super(message);
    }

    public ProxyPoolException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProxyPoolException(final Throwable cause) {
        super(cause);
    }

    public ProxyPoolException(final String message, final Throwable cause, final boolean enableSuppression,
                              final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
