package com.ag.proxy.pool;


import com.ag.proxy.Proxy;

public interface ProxyPool {

    Proxy acquireProxy() throws InterruptedException;
    void releaseProxy(final Proxy proxy);
    long size();
    long available();
}
