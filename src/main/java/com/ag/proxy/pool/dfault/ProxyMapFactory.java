package com.ag.proxy.pool.dfault;

import com.ag.proxy.ProxyFactory;
import com.ag.proxy.info.ProxyInfo;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ProxyMapFactory {

    private final ProxyFactory factory;

    ProxyMapFactory(final ProxyFactory factory) {
        this.factory = factory;
    }

    ConcurrentMap<Proxy, State> create(final List<ProxyInfo> infos) {
        final List<Proxy> proxies = this.factory.apply(infos);
        final ConcurrentMap<Proxy, State> map = new ConcurrentHashMap<>(proxies.size());
        proxies.forEach(p -> map.put(p, State.AVAILABLE));
        return map;
    }
}
