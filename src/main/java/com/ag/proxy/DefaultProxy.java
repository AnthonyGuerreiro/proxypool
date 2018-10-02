package com.ag.proxy;

import lombok.Value;

@Value
public class DefaultProxy implements Proxy {

    private String host;
    private int port;
}
