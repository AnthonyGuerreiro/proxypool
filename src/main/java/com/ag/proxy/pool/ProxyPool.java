package com.ag.proxy.pool;

import java.net.Proxy;

public interface ProxyPool {
    Proxy acquireProxy() throws InterruptedException;
    void releaseProxy(final Proxy proxy);
    long size();
    long available();
}
