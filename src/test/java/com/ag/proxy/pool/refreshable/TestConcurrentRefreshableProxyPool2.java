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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestConcurrentRefreshableProxyPool2 {

    private final DefaultProxyPoolFactory factory =
            new DefaultProxyPoolFactory(new ProxyInfoMapper(), RoundRobinSelectionStrategy::new);

    private RefreshableProxyPool pool() {

        final Proxy proxy = new DefaultProxy("mapped-url", 8888);
        final ProxyInfo info = new DefaultProxyInfo("url", new NullProxyExtractor());

        final Supplier<ProxyPool> supplier = () -> this.factory.createDefaultProxyPool(singletonList(info),
                                                                                       null,
                                                                                       ignored -> singletonList(proxy));
        return new RefreshableProxyPool(supplier);
    }

    @Test
    public void testRefreshBlocked() throws ExecutionException, InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(10);

        final RefreshableProxyPool pool = pool();
        final Callable<Proxy> acquire = pool::acquireProxy;
        final Callable<Void> refresh = () -> {
            pool.refresh();
            return null;
        };

        final Future<Proxy> futureProxy = service.submit(acquire);

        assertThat(futureProxy.get(), is(instanceOf(Proxy.class)));
        final Future<Proxy> futureException = service.submit(acquire);
        service.submit(refresh);
        try {
            futureException.get();
            throw new AssertionError("Expected error message");
        } catch (final InterruptedException | ExecutionException e) {
            // nop
        }
        final Future<Proxy> anotherFutureProxy = service.submit(acquire);
        assertThat(anotherFutureProxy.get(), is(instanceOf(Proxy.class)));

        service.shutdown();
        service.awaitTermination(2L, TimeUnit.SECONDS);
    }
}
