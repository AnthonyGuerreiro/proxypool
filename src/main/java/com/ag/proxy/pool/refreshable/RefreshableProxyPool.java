package com.ag.proxy.pool.refreshable;

import com.ag.proxy.pool.ProxyPool;
import net.jcip.annotations.GuardedBy;

import java.net.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class RefreshableProxyPool implements ProxyPool {

    @GuardedBy("lock")
    private final Supplier<ProxyPool> supplier;

    @GuardedBy("lock")
    private ProxyPool pool;

    @GuardedBy("lock")
    private int version = 0;
    @GuardedBy("lock")
    private final ConcurrentMap<Proxy, VersionedProxy> cache;
    @GuardedBy("lock")
    private final ConcurrentMap<VersionedProxy, Proxy> reverseCache;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = this.lock.readLock();
    private final Lock w = this.lock.writeLock();


    public RefreshableProxyPool(final Supplier<ProxyPool> supplier) {
        this.supplier = supplier;
        this.pool = this.supplier.get();

        final int size = (int) Math.min(this.pool.size(), Integer.MAX_VALUE);
        this.cache = new ConcurrentHashMap<>(size);
        this.reverseCache = new ConcurrentHashMap<>(size);
    }

    @SuppressWarnings("unused")
    public void refresh() {
        this.w.lock();
        try {
            this.version++;
            this.reverseCache.clear();
            this.cache.clear();
            this.pool = this.supplier.get();
        } finally {
            this.w.unlock();
        }
    }

    @Override
    public Proxy acquireProxy() throws InterruptedException {
        this.r.lock();
        try {
            final Proxy proxy = this.pool.acquireProxy();
            final VersionedProxy vProxy = this.cache.computeIfAbsent(proxy,
                                                                     pr -> new VersionedProxy(pr, this.version));
            this.reverseCache.putIfAbsent(vProxy, proxy);
            return vProxy;
        } finally {
            this.r.unlock();
        }
    }

    @Override
    public void releaseProxy(final Proxy proxy) {
        if (!(proxy instanceof VersionedProxy)) {
            return;
        }
        this.r.lock();
        try {
            if (((VersionedProxy) proxy).getVersion() != this.version) {
                return;
            }
            this.pool.releaseProxy(this.reverseCache.get(proxy));
        } finally {
            this.r.unlock();
        }
    }

    @Override
    public long size() {
        this.r.lock();
        try {
            return this.pool.size();
        } finally {
            this.r.unlock();
        }
    }

    @Override
    public long available() {
        this.r.lock();
        try {
            return this.pool.available();
        } finally {
            this.r.unlock();
        }
    }
}
