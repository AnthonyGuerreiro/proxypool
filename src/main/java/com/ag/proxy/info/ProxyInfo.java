package com.ag.proxy.info;

import com.ag.proxy.extractor.ProxyExtractor;

import java.util.List;

public interface ProxyInfo {

    String getUrl();
    ProxyExtractor getExtractor();

    default List<String> extract(final String url) {
         return getExtractor().extract(url);
    }
}
