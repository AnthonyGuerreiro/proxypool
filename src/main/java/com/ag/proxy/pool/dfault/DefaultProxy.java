package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import lombok.Value;

@Value
public class DefaultProxy implements Proxy {

    private String host;
    private int port;
}
