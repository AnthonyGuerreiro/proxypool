package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import com.ag.proxy.info.ProxyInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

class ProxyMapFactory {

    private final Function<List<ProxyInfo>, List<Proxy>> factory;

    ProxyMapFactory(final Function<List<ProxyInfo>, List<Proxy>> factory) {
        this.factory = factory;
    }

    ConcurrentMap<Proxy, State> create(final List<ProxyInfo> infos) {
        final List<Proxy> proxies = this.factory.apply(infos);
        final ConcurrentMap<Proxy, State> map = new ConcurrentHashMap<>(proxies.size());
        proxies.forEach(p -> map.put(p, State.AVAILABLE));
        return map;
    }
}
