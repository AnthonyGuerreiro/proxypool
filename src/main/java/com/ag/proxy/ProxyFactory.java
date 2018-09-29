package com.ag.proxy;

import com.ag.proxy.info.ProxyInfo;

import java.net.Proxy;
import java.util.List;
import java.util.function.Function;

public interface ProxyFactory extends Function<List<ProxyInfo>, List<Proxy>> {

}
