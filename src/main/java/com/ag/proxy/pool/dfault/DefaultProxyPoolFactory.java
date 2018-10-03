package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.pool.ProxyPool;
import com.ag.proxy.strategy.ItemSelectionStrategy;
import com.ag.proxy.supplier.ProxySupplier;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DefaultProxyPoolFactory {

    private final static ProxySupplier DEFAULT_PROXIES = new ProxySupplier();

    private final Function<ProxyInfo, List<Proxy>> mapper;
    private final Supplier<ItemSelectionStrategy<Proxy>> defaultSelectionStrategy;

    @SuppressWarnings("WeakerAccess")
    public ProxyPool createDefaultProxyPool(final List<ProxyInfo> infos,
                                            final ItemSelectionStrategy<Proxy> strategy,
                                            final Function<ProxyInfo, List<Proxy>> mapper) {

        final List<ProxyInfo> selectedInfos = infos == null || infos.isEmpty() ? DEFAULT_PROXIES.get() : infos;
        final ItemSelectionStrategy<Proxy> selectedStrategy = strategy != null ? strategy : this.defaultSelectionStrategy.get();
        final Function<ProxyInfo, List<Proxy>> selectedMapper = mapper != null ? mapper : this.mapper;

        return new DefaultProxyPool(selectedInfos, selectedStrategy, selectedMapper);
    }
}
