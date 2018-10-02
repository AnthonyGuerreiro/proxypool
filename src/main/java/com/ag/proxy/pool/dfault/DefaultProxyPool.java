package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.pool.ProxyPool;
import com.ag.proxy.strategy.ItemSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;

public class DefaultProxyPool implements ProxyPool {

    private final long size;

    private final Semaphore semaphore;
    private final ConcurrentMap<Proxy, State> proxies;
    private final List<Proxy> exposableProxyList;

    private final Consumer<Proxy> freeProxy;
    private final Function<Proxy, Boolean> reserveProxy;

    private final ItemSelectionStrategy<Proxy> strategy;

    public DefaultProxyPool(final List<ProxyInfo> infos,
                            final ItemSelectionStrategy<Proxy> strategy,
                            final Function<List<ProxyInfo>, List<Proxy>> proxyFactory) {
        this.strategy = strategy;

        this.proxies = new ProxyMapFactory(proxyFactory).create(infos);
        this.exposableProxyList = Collections.unmodifiableList(new ArrayList<>(this.proxies.keySet()));
        this.size = this.exposableProxyList.size();

        this.reserveProxy = proxy -> this.proxies.replace(proxy, State.AVAILABLE, State.TAKEN);
        this.freeProxy = proxy -> this.proxies.put(proxy, State.AVAILABLE);
        this.semaphore = new Semaphore(this.proxies.size());
    }


    @Override
    public Proxy acquireProxy() throws InterruptedException {
        this.semaphore.acquire();
        return this.strategy.select(this.exposableProxyList, this.reserveProxy);
    }

    @Override
    public void releaseProxy(final Proxy proxy) {
        if (proxy == null || this.proxies.get(proxy) != State.TAKEN) {
            return;
        }
        this.freeProxy.accept(proxy);
        this.semaphore.release();
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public long available() {
        return this.semaphore.availablePermits();
    }
}
