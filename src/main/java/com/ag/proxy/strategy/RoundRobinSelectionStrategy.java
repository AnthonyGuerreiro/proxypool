package com.ag.proxy.strategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RoundRobinSelectionStrategy<T> implements ItemSelectionStrategy<T> {

    private final Map<Collection<T>, Integer> cache = new ConcurrentHashMap<>(10);

    @Override
    public T select(final List<T> ts, final Function<T, Boolean> reserveFunction) {
        final int start = computeStart(ts);

        for (int i = start; i < ts.size(); i++) {
            final T t = ts.get(i);
            if (reserveFunction.apply(t)) {
                this.cache.replace(ts, start, i);
                return t;
            }
        }

        throw new IllegalArgumentException("Precondition failed: At least one element must be available");
    }

    private int computeStart(final List<T> ts) {
        final int start = this.cache.computeIfAbsent(ts, t -> 0);
        return start < ts.size() ? start : 0;
    }
}
