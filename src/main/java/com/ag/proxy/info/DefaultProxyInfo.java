package com.ag.proxy.info;

import com.ag.proxy.extractor.ProxyExtractor;
import lombok.Value;

@Value
public class DefaultProxyInfo implements ProxyInfo {

    private String url;

    private ProxyExtractor extractor;
}
