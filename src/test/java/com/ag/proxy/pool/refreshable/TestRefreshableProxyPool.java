package com.ag.proxy.pool.refreshable;

import com.ag.proxy.pool.dfault.DefaultProxyPoolFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestRefreshableProxyPool {

    @Test
    public void testInstantiation() {
        final DefaultProxyPoolFactory factory = new DefaultProxyPoolFactory(null, null);
        final RefreshableProxyPool pool = new RefreshableProxyPool(factory::createDefaultProxyPool);
        assertThat(pool.size(), is(not(0)));
    }
}
