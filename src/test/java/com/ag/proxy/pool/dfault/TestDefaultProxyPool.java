package com.ag.proxy.pool.dfault;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestDefaultProxyPool {

    @Test
    public void testInstantiation() {
        final DefaultProxyPoolFactory factory = new DefaultProxyPoolFactory(null, null);
        final DefaultProxyPool pool = factory.createDefaultProxyPool();
        assertThat(pool.size(), is(not(0)));
    }
}
