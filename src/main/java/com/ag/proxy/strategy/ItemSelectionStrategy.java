package com.ag.proxy.strategy;

import java.util.List;
import java.util.function.Function;

public interface ItemSelectionStrategy<T> {

    T select(List<T> ts, Function<T, Boolean> reserveFunction);
}
