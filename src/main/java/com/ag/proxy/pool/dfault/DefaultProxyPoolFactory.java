package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import com.ag.proxy.ProxyInfoMapper;
import com.ag.proxy.factory.DefaultProxyFactory;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.strategy.ItemSelectionStrategy;
import com.ag.proxy.strategy.RoundRobinSelectionStrategy;
import com.ag.proxy.supplier.ProxySupplier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultProxyPoolFactory {

    private final static ProxySupplier DEFAULT_PROXIES = new ProxySupplier();

    private final Function<ProxyInfo, List<Proxy>> mapper;
    private final Supplier<ItemSelectionStrategy<Proxy>> defaultSelectionStrategy;

    public DefaultProxyPoolFactory(final Function<ProxyInfo, List<Proxy>> mapper,
                                   final Supplier<ItemSelectionStrategy<Proxy>> supplier) {

        this.mapper = mapper != null ? mapper : new ProxyInfoMapper();
        this.defaultSelectionStrategy = supplier != null ? supplier : RoundRobinSelectionStrategy::new;
    }

    public DefaultProxyPool createDefaultProxyPool() {
        return createDefaultProxyPool(null, null, null);
    }

    @SuppressWarnings("WeakerAccess")
    public DefaultProxyPool createDefaultProxyPool(final List<ProxyInfo> infos,
                                                   final ItemSelectionStrategy<Proxy> strategy,
                                                   final Function<List<ProxyInfo>, List<Proxy>> factory) {

        return new DefaultProxyPool(getProxyInfos(infos), getStrategy(strategy), getProxyFactory(factory));
    }

    private Function<List<ProxyInfo>, List<Proxy>> getProxyFactory(
            final Function<List<ProxyInfo>, List<Proxy>> factory) {
        return factory != null ? factory : new DefaultProxyFactory(this.mapper);
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
