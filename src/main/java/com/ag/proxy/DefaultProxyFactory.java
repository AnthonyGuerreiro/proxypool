package com.ag.proxy;

import com.ag.proxy.info.ProxyInfo;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DefaultProxyFactory implements ProxyFactory {

    @Override
    public List<Proxy> apply(final List<ProxyInfo> infos) {
        return ensureAtLeastOne(toProxies(extractUrls(infos)));
    }

    private List<String> extractUrls(final List<ProxyInfo> infos) {
        return infos.stream()
                .map(info -> info.extract(info.getUrl()))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<Proxy> ensureAtLeastOne(final List<Proxy> proxies) {
        return proxies.isEmpty() ? Collections.singletonList(Proxy.NO_PROXY) : proxies;
    }

    private List<Proxy> toProxies(final List<String> urls) {
        return urls.stream()
                .map(s -> s.split(":"))
                .map(ss -> new InetSocketAddress(ss[0], Integer.parseInt(ss[1])))
                .map(address -> new Proxy(Proxy.Type.HTTP, address))
                .collect(toList());
    }
}
