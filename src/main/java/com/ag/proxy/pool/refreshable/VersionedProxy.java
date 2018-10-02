package com.ag.proxy.pool.refreshable;

import com.ag.proxy.Proxy;
import lombok.Getter;

class VersionedProxy implements Proxy {

    private final Proxy proxy;

    @Getter
    private final int version;

    public VersionedProxy(final Proxy proxy, final int version) {
        this.proxy = proxy;
        this.version = version;
    }

    @Override
    public String getHost() {
        return this.proxy.getHost();
    }

    @Override
    public int getPort() {
        return this.proxy.getPort();
    }
}
