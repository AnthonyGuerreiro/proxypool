package com.ag.proxy.pool.unchecked;

import com.ag.proxy.Proxy;
import com.ag.proxy.pool.ProxyPool;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SneakyProxyPool implements ProxyPool {

    private final ProxyPool pool;

    @Override
    public Proxy acquireProxy() {
        try {
            return this.pool.acquireProxy();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseProxy(final Proxy proxy) {
        this.pool.releaseProxy(proxy);
    }

    @Override
    public long size() {
        return this.pool.size();
    }

    @Override
    public long available() {
        return this.pool.available();
    }
}
