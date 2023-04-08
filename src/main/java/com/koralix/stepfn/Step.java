package com.koralix.stepfn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class Step<T, R> implements Function<T, R> {

    protected final Map<Step<?, T>, T> aggregation = new HashMap<>();
    protected T stepFunctionInput;

    public void aggregate(Step<?, T> from, T input) {
        if (from == null)
            this.stepFunctionInput = input;
        else
            this.aggregation.put(from, input);
    }

    public abstract boolean isComplete();

}
