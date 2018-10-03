package com.ag.proxy.pool.refreshable;

import com.ag.proxy.Proxy;
import com.ag.proxy.extractor.NullProxyExtractor;
import com.ag.proxy.info.DefaultProxyInfo;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.info.ProxyInfoMapper;
import com.ag.proxy.pool.ProxyPool;
import com.ag.proxy.pool.dfault.DefaultProxy;
import com.ag.proxy.pool.dfault.DefaultProxyPoolFactory;
import com.ag.proxy.strategy.RoundRobinSelectionStrategy;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestBaseRefreshableProxyPool {

    private final static String URL = "url";
    private final static String MAPPED_URL = "mapped-url";
    private final static int PORT = 7777;


    private final static List<ProxyInfo> NULL_ENTRY =
            Collections.singletonList(new DefaultProxyInfo(URL, new NullProxyExtractor()));

    private final static Function<ProxyInfo, List<Proxy>> NULL_MAPPER =
            info -> Collections.singletonList(new DefaultProxy(MAPPED_URL, PORT));


    private final DefaultProxyPoolFactory factory =
            new DefaultProxyPoolFactory(new ProxyInfoMapper(), RoundRobinSelectionStrategy::new);

    private final Supplier<ProxyPool> supplier =
            () -> this.factory.createDefaultProxyPool(NULL_ENTRY, null, NULL_MAPPER);

    @Test
    public void testDefaultInstantiation() throws InterruptedException {

        final ProxyPool pool = new RefreshableProxyPool(this.supplier);

        assertThat(pool.size(), is(1L));
        assertThat(pool.available(), is(1L));

        final Proxy proxy = pool.acquireProxy();
        assertThat(proxy.getHost(), is(MAPPED_URL));
        assertThat(proxy.getPort(), is(PORT));

        assertThat(pool.size(), is(1L));
        assertThat(pool.available(), is(0L));
    }

    @Test
    public void testRefresh() throws InterruptedException {
        final ProxyPool pool = new RefreshableProxyPool(this.supplier);
        final Proxy firstProxy = pool.acquireProxy();

        ((RefreshableProxyPool) pool).refresh();
        assertThat(pool.size(), is(1L));
        assertThat(pool.available(), is(1L));

        final Proxy secondProxy = pool.acquireProxy();
        assertThat(firstProxy, not(sameInstance(secondProxy)));
        assertThat(pool.size(), is(1L));
        assertThat(pool.available(), is(0L));
    }
}
