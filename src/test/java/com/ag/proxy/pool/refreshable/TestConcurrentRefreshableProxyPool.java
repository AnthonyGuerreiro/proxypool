package com.ag.proxy.pool.refreshable;

import com.ag.proxy.Proxy;
import com.ag.proxy.extractor.NullProxyExtractor;
import com.ag.proxy.info.DefaultProxyInfo;
import com.ag.proxy.info.ProxyInfo;
import com.ag.proxy.info.ProxyInfoMapper;
import com.ag.proxy.pool.dfault.DefaultProxy;
import com.ag.proxy.pool.dfault.DefaultProxyPoolFactory;
import com.ag.proxy.strategy.RoundRobinSelectionStrategy;
import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.MethodOption;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestConcurrentRefreshableProxyPool {

    private final DefaultProxyPoolFactory factory =
            new DefaultProxyPoolFactory(new ProxyInfoMapper(), RoundRobinSelectionStrategy::new);

    private RefreshableProxyPool pool;

    @ThreadedBefore
    public void before() {

        final Proxy proxy = new DefaultProxy("mapped-url", 8888);
        final ProxyInfo info = new DefaultProxyInfo("url", new NullProxyExtractor());

        this.pool = new RefreshableProxyPool(() -> this.factory.createDefaultProxyPool(singletonList(info),
                                                                                       null,
                                                                                       ignored -> singletonList(
                                                                                               proxy)));
    }

    @ThreadedMain
    public void mainThread() throws InterruptedException {
        try {
            final Proxy proxy = this.pool.acquireProxy();
        } catch (final IllegalThreadStateException e) {
            throw new InterruptedException();
        }
        this.pool.refresh();
        assertThat(this.pool.available(), is(1L));
    }

    @ThreadedSecondary
    public void secondThread() throws InterruptedException {
        mainThread();
    }

    @Test
    public void testConcurrent() {
        final AnnotatedTestRunner runner = new AnnotatedTestRunner();
        final Set<String> methods = new HashSet<>(10);
        runner.setMethodOption(MethodOption.ALL_METHODS, methods);
        try {
            runner.runTests(getClass(), RefreshableProxyPool.class);
        } catch (final RuntimeException e) {
            final String expectedMessage = "Cannot step when call depth is 0";

            assertThat(e.getCause(), instanceOf(InvocationTargetException.class));
            assertThat(e.getCause().getCause(), instanceOf(IllegalStateException.class));

            final String message = e.getCause().getCause().getMessage();
            assertThat(message, is(expectedMessage));
        }
    }
}
