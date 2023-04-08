package com.koralix.stepfn;

public interface Transition<T, R> {

    boolean isApplicable(T input);

    Step<R, ?> get();

}
