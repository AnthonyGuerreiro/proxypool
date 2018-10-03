package com.ag.proxy.pool.dfault;

import com.ag.proxy.Proxy;
import com.ag.proxy.extractor.NullProxyExtractor;
import com.ag.proxy.info.DefaultProxyInfo;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.info.ProxyInfoMapper;
import com.ag.proxy.strategy.RoundRobinSelectionStrategy;
import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.ThreadedAfter;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestConcurrentDefaultProxyPool {

    private final DefaultProxyPoolFactory factory =
            new DefaultProxyPoolFactory(new ProxyInfoMapper(), RoundRobinSelectionStrategy::new);

    private DefaultProxyPool pool;

    @ThreadedBefore
    public void before() {

        final Proxy proxy = new DefaultProxy("mapped-url", 8888);
        final ProxyInfo info = new DefaultProxyInfo("url", new NullProxyExtractor());

        this.pool = (DefaultProxyPool) this.factory.createDefaultProxyPool(singletonList(info),
                                                                           null,
                                                                           ignored -> singletonList(proxy));
    }

    @ThreadedMain
    public void mainThread() throws InterruptedException {
        final Proxy proxy = this.pool.acquireProxy();
        this.pool.releaseProxy(proxy);
    }

    @ThreadedSecondary
    public void secondThread() throws InterruptedException {
        mainThread();
    }

    @ThreadedAfter
    public void after() {
        assertThat(this.pool.available(), is(1L));
    }

    @Test
    public void testConcurrent() {
        final AnnotatedTestRunner runner = new AnnotatedTestRunner();
        runner.runTests(getClass(), DefaultProxyPool.class);
    }
}
