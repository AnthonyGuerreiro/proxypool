package com.ag.proxy.factory;

import com.ag.proxy.Proxy;
import com.ag.proxy.info.ProxyInfo;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class DefaultProxyFactory implements Function<List<ProxyInfo>, List<Proxy>> {

    private final Function<ProxyInfo, List<Proxy>> mapper;

    @Override
    public List<Proxy> apply(final List<ProxyInfo> infos) {
        return infos.stream()
                .map(this.mapper)
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
