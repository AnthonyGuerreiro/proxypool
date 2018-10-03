package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import com.ag.proxy.info.ProxyInfo;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static java.util.stream.Collectors.toConcurrentMap;

class ProxyInfoToConcurrentMapConverter {

    private final Function<ProxyInfo, List<Proxy>> mapper;

    ProxyInfoToConcurrentMapConverter(final Function<ProxyInfo, List<Proxy>> mapper) {
        this.mapper = mapper;
    }

    ConcurrentMap<Proxy, State> convert(final List<ProxyInfo> infos) {
        return infos.stream()
                .map(this.mapper)
                .flatMap(Collection::stream)
                .collect(toConcurrentMap(Function.identity(), ign -> State.AVAILABLE));
    }
}
