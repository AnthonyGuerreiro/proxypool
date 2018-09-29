package com.ag.proxy.extractor;

import java.util.List;

public interface ProxyExtractor {

    List<String> extract(final String url);
}
