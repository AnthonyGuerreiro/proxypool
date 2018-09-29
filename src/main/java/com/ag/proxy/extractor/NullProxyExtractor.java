package com.ag.proxy.extractor;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class NullProxyExtractor implements ProxyExtractor {

    @Override
    public List<String> extract(final String url) {
        return Collections.singletonList(url);
    }
}
