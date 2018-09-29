package com.ag.proxy.pool.dfault;

import com.ag.proxy.DefaultProxies;
import com.ag.proxy.DefaultProxyFactory;
import com.ag.proxy.ProxyFactory;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.strategy.ItemSelectionStrategy;
import com.ag.proxy.strategy.RoundRobinSelectionStrategy;

import java.net.Proxy;
import java.util.List;
import java.util.function.Supplier;

public class DefaultProxyPoolFactory {

    private final Supplier<ItemSelectionStrategy<Proxy>> defaultSelectionStrategy;
    private final static DefaultProxies DEFAULT_PROXIES = new DefaultProxies();

    public DefaultProxyPoolFactory(final Supplier<ItemSelectionStrategy<Proxy>> supplier) {
        this.defaultSelectionStrategy = supplier != null ? supplier : RoundRobinSelectionStrategy::new;
    }

    public DefaultProxyPool createDefaultProxyPool() {
        return createDefaultProxyPool(null, null, null);
    }

    @SuppressWarnings("WeakerAccess")
    public DefaultProxyPool createDefaultProxyPool(final List<ProxyInfo> infos,
                                                   final ItemSelectionStrategy<Proxy> strategy,
                                                   final ProxyFactory factory) {

        return new DefaultProxyPool(getProxyInfos(infos), getStrategy(strategy), getProxyFactory(factory));
    }

    private ProxyFactory getProxyFactory(final ProxyFactory factory) {
        return factory != null ? factory : new DefaultProxyFactory();
    }

    private List<ProxyInfo> getProxyInfos(final List<ProxyInfo> inputInfos) {
        if (inputInfos != null && !inputInfos.isEmpty()) {
            return inputInfos;
        }
        return DEFAULT_PROXIES.defaultProxyInfos();
    }

    private ItemSelectionStrategy<Proxy> getStrategy(final ItemSelectionStrategy<Proxy> inputStrategy) {
        return inputStrategy != null ? inputStrategy : this.defaultSelectionStrategy.get();
    }
}
