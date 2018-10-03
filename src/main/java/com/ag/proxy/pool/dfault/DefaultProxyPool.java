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
import java.util.function.Function;

public class DefaultProxyPool implements ProxyPool {

    private final long size;

    private final Semaphore semaphore;
    private final ConcurrentMap<Proxy, State> proxies;
    private final List<Proxy> exposableProxyList;

    private final ItemSelectionStrategy<Proxy> strategy;

    public DefaultProxyPool(final List<ProxyInfo> infos,
                            final ItemSelectionStrategy<Proxy> strategy,
                            final Function<ProxyInfo, List<Proxy>> mapper) {

        this.strategy = strategy;
        this.proxies = new ProxyInfoToConcurrentMapConverter(mapper).convert(infos);

        this.exposableProxyList = Collections.unmodifiableList(new ArrayList<>(this.proxies.keySet()));
        this.size = this.exposableProxyList.size();

        this.semaphore = new Semaphore(this.exposableProxyList.size());
    }


    @Override
    public Proxy acquireProxy() throws InterruptedException {
        final Function<Proxy, Boolean> reserve = proxy -> this.proxies.replace(proxy, State.AVAILABLE, State.TAKEN);

        this.semaphore.acquire();
        return this.strategy.select(this.exposableProxyList, reserve);
    }

    @Override
    public boolean releaseProxy(final Proxy proxy) {
        if (proxy == null) {
            return false;
        }
        final boolean replaced = this.proxies.replace(proxy, State.TAKEN, State.AVAILABLE);
        if (replaced) {
            this.semaphore.release();
        }
        return replaced;
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
