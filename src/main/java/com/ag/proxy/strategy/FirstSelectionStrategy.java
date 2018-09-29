package com.ag.proxy.strategy;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class FirstSelectionStrategy<T> implements ItemSelectionStrategy<T> {

    @Override
    public T select(final List<T> ts, final Function<T, Boolean> reserveFunction) {
        for (final T t : ts) {
            if (reserveFunction.apply(t)) {
                return t;
            }
        }

        throw new IllegalArgumentException("Precondition failed: At least one element must be available");
    }
}
