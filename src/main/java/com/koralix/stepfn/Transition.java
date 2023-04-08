package com.koralix.stepfn;

public interface Transition<T> {

    boolean isApplicable(T input);

    Step<T, ?> get();

}
