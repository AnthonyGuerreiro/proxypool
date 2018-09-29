package com.ag.proxy;

import com.ag.proxy.extractor.ClasspathUrlProxyExtractor;
import com.ag.proxy.extractor.ProxyExtractor;
import com.ag.proxy.extractor.RawUrlProxyExtractor;
import com.ag.proxy.info.DefaultProxyInfo;
import com.ag.proxy.info.ProxyInfo;

import java.util.Collections;
import java.util.List;

public class DefaultProxies {

    private final static String DEFAULT_URL = "/raw-proxy-urls.txt";

    public List<ProxyInfo> defaultProxyInfos() {
        final ProxyExtractor extractor = new ClasspathUrlProxyExtractor(new RawUrlProxyExtractor(" "));
        return Collections.singletonList(new DefaultProxyInfo(DEFAULT_URL, extractor));
    }
}
