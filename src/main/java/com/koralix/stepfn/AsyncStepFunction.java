package com.koralix.stepfn;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsyncStepFunction<T, R> extends StepFunction<T, R, CompletableFuture<R>> {

    private final Executor executor;

    public AsyncStepFunction(
            Step<T, ?> start,
            Map<Step<?, ?>, Set<Transition<?>>> transitions,
            Executor executor
    ) {
        super(start, transitions);
        this.executor = executor;
    }

    public AsyncStepFunction(Step<T, ?> start, Executor executor) {
        super(start);
        this.executor = executor;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        CompletableFuture<R> future = new CompletableFuture<>();
        // TODO: check that null is stored in the aggregation map
        //       may be a problem to read the value from the map
        this.apply(this.firstStep(), null, t, future);
        return future;
    }

    @Override
    protected <A, B> Optional<CompletableFuture<B>> step(Step<A, B> step, Step<?, A> from, A input) {
        step.aggregate(from, input);
        if (step.isComplete())
            return Optional.of(CompletableFuture.supplyAsync(() -> step.apply(input), this.executor));
        else
            return Optional.empty();
    }
}
