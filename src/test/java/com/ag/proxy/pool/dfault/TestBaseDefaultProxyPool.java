package com.ag.proxy.pool.dfault;

import com.ag.proxy.info.ProxyInfoMapper;
import com.ag.proxy.pool.ProxyPool;
import com.ag.proxy.strategy.RoundRobinSelectionStrategy;
import com.ag.proxy.supplier.ProxySupplier;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestBaseDefaultProxyPool {


    private final DefaultProxyPoolFactory factory =
            new DefaultProxyPoolFactory(new ProxyInfoMapper(), RoundRobinSelectionStrategy::new);

    @Test
    public void testDefaultInstantiation() throws InterruptedException {

        final ProxyPool pool = this.factory.createDefaultProxyPool(new ProxySupplier().get(),
                                                                   new RoundRobinSelectionStrategy<>(),
                                                                   new ProxyInfoMapper());
        assertThat(pool.size(), is(not(0)));

        final long available = pool.available();
        pool.acquireProxy();
        assertThat(pool.available(), is(available - 1));
    }

    @Test
    public void testEmptyProxyPoolInstantiation() {
        final ProxyPool pool = this.factory.createDefaultProxyPool(new ProxySupplier().get(),
                                                                   null,
                                                                   ignored -> Collections.emptyList());

        assertThat(pool.size(), is(0L));
    }
}
