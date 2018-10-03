package com.ag.proxy.info;

import com.ag.proxy.pool.dfault.DefaultProxy;
import com.ag.proxy.Proxy;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ProxyInfoMapper implements Function<ProxyInfo, List<Proxy>> {

    @Override
    public List<Proxy> apply(final ProxyInfo info) {
        final List<String> urls = info.extract(info.getUrl());
        return urls.stream()
                .map(this::map)
                .collect(toList());
    }

    private Proxy map(final String url) {
        final String[] parts = url.split(":");
        final String host = parts[0];
        final int port = Integer.parseInt(parts[1]);
        return new DefaultProxy(host, port);
    }
}
