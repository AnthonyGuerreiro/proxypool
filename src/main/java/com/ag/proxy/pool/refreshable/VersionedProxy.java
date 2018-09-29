package com.ag.proxy.pool.refreshable;

import lombok.Getter;

import java.net.Proxy;

@Getter
class VersionedProxy extends Proxy {

    private final Proxy proxy;
    private final int version;

    public VersionedProxy(final Proxy proxy, final int version) {
        super(proxy.type(), proxy.address());
        this.proxy = proxy;
        this.version = version;
    }
}
