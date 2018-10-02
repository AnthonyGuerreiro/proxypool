package com.ag.proxy;

import com.ag.proxy.info.ProxyInfo;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DefaultProxyFactory implements ProxyFactory {

    @Override
    public List<Proxy> apply(final List<ProxyInfo> infos) {
        return toProxies(extractUrls(infos));
    }

    private List<String> extractUrls(final List<ProxyInfo> infos) {
        return infos.stream()
                .map(info -> info.extract(info.getUrl()))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<Proxy> toProxies(final List<String> urls) {
        return urls.stream()
                .map(s -> s.split(":"))
                .map(ss -> new DefaultProxy(ss[0], Integer.parseInt(ss[1])))
                .collect(toList());
    }
}
