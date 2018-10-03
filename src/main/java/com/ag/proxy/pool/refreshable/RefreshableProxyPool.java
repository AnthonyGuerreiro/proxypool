package com.ag.proxy.pool.refreshable;

import com.ag.proxy.Proxy;
import com.ag.proxy.pool.ProxyPool;
import net.jcip.annotations.GuardedBy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

public class RefreshableProxyPool implements ProxyPool {

    private final StampedLock sl = new StampedLock();

    @GuardedBy("sl")
    private final Supplier<ProxyPool> supplier;

    @GuardedBy("sl")
    private ProxyPool pool;

    @GuardedBy("sl")
    private int version = 0;
    @GuardedBy("sl")
    private final ConcurrentMap<Proxy, VersionedProxy> cache;
    @GuardedBy("sl")
    private final ConcurrentMap<VersionedProxy, Proxy> reverseCache;


    public RefreshableProxyPool(final Supplier<ProxyPool> supplier) {
        this.supplier = supplier;
        this.pool = this.supplier.get();

        final int size = (int) Math.min(this.pool.size(), Integer.MAX_VALUE);
        this.cache = new ConcurrentHashMap<>(size);
        this.reverseCache = new ConcurrentHashMap<>(size);
    }

    public void refresh() {
        final long stamp = this.sl.writeLock();
        try {
            this.version++;
            this.cache.clear();
            this.reverseCache.clear();
            this.pool = this.supplier.get();
        } finally {
            this.sl.unlockWrite(stamp);
        }
    }

    @Override
    public Proxy acquireProxy() throws InterruptedException {
        final long stamp = this.sl.readLock();
        try {
            final Proxy proxy = pool.acquireProxy();
            final VersionedProxy vProxy = this.cache.computeIfAbsent(proxy, pr -> new VersionedProxy(pr, this.version));
            this.reverseCache.putIfAbsent(vProxy, proxy);
            return vProxy;
        } finally {
            this.sl.unlockRead(stamp);
        }
    }

    @Override
    public boolean releaseProxy(final Proxy proxy) {
        if (!(proxy instanceof VersionedProxy)) {
            return false;
        }

        long stamp = this.sl.tryOptimisticRead();
        boolean released = releaseProxyInternal(proxy);
        if (!this.sl.validate(stamp)) {
            stamp = this.sl.readLock();
            try {
                released = releaseProxyInternal(proxy);
            } finally {
                this.sl.unlockRead(stamp);
            }
        }
        return released;
    }

    private boolean releaseProxyInternal(final Proxy proxy) {
        if (((VersionedProxy) proxy).getVersion() != this.version) {
            return false;
        }
        return this.pool.releaseProxy(this.reverseCache.get(proxy));
    }

    @Override
    public long size() {
        long stamp = this.sl.tryOptimisticRead();
        long size = this.pool.size();
        if (!this.sl.validate(stamp)) {
            stamp = this.sl.readLock();
            try {
                size = this.pool.size();
            } finally {
                this.sl.unlockRead(stamp);
            }
        }
        return size;
    }

    @Override
    public long available() {
        long stamp = this.sl.tryOptimisticRead();
        long available = this.pool.available();
        if (!this.sl.validate(stamp)) {
            try {
                stamp = this.sl.readLock();
                available = this.pool.available();
            } finally {
                this.sl.unlockRead(stamp);
            }
        }
        return available;
    }
}
