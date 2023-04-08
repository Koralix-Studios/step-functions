package com.koralix.stepfn;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SyncStepFunction<T, R> extends StepFunction<T, R, R> {

    public SyncStepFunction(Step<T, ?> first, Map<Step<?, ?>, Set<Transition<?>>> transitions) {
        super(first, transitions);
    }

    public SyncStepFunction(Step<T, ?> first) {
        super(first);
    }

    @Override
    public R apply(T t) {
        CompletableFuture<R> future = new CompletableFuture<>();
        // TODO: check that null is stored in the aggregation map
        //       may be a problem to read the value from the map
        this.apply(this.firstStep(), null, t, future);
        return future.join();
    }

    @Override
    protected <A, B> Optional<CompletableFuture<B>> step(Step<A, B> step, Step<?, A> from, A input) {
        step.aggregate(from, input);
        if (step.isComplete())
            return Optional.of(CompletableFuture.completedFuture(step.apply(input)));
        else
            return Optional.empty();
    }
}
