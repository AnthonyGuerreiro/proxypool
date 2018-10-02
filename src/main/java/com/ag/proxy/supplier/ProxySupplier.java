package com.ag.proxy.supplier;

import com.ag.proxy.extractor.ClasspathUrlProxyExtractor;
import com.ag.proxy.extractor.ProxyExtractor;
import com.ag.proxy.extractor.RawUrlProxyExtractor;
import com.ag.proxy.info.DefaultProxyInfo;
import com.ag.proxy.info.ProxyInfo;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ProxySupplier implements Supplier<List<ProxyInfo>> {

    private final static String DEFAULT_URL = "/raw-proxy-urls.txt";

    public List<ProxyInfo> defaultProxyInfos() {
        final ProxyExtractor extractor = new ClasspathUrlProxyExtractor(new RawUrlProxyExtractor(" "));
        return Collections.singletonList(new DefaultProxyInfo(DEFAULT_URL, extractor));
    }

    @Override
    public List<ProxyInfo> get() {
        return defaultProxyInfos();
    }
}
