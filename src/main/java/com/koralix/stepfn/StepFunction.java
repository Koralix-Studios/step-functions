package com.koralix.stepfn;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class StepFunction<T, V, R> {

    private final Step<T, ?> first;
    private final Map<Step<?, ?>, Collection<Transition<?, ?>>> transitions;

    public StepFunction(
            Step<T, ?> first, Map<Step<?, ?>,
            Collection<Transition<?, ?>>> transitions
    ) {
        this.first = first;
        this.transitions = transitions;
    }

    public abstract R apply(T t);

    @SuppressWarnings("unchecked")
    protected <A, B> void apply(Step<A, B> step, Step<?, A> from, A input, CompletableFuture<V> future) {
        this.step(step, from, input).ifPresent(completableFuture -> {
            step.aggregation.clear();
            step.stepFunctionInput = null;

            completableFuture.thenAccept(b -> {
                Collection<Transition<?, ?>> transitions = this.transitions(step);
                if (transitions == null) {
                    future.complete((V) b);
                    return;
                }
                List<? extends Step<B, ?>> nextSteps = transitions.stream()
                        .map(transition -> (Transition<B, ?>) transition)
                        .filter(transition -> transition.isApplicable(b))
                        .map(Transition::get)
                        .map(next -> (Step<B, ?>) next)
                        .toList();
                if (nextSteps.isEmpty())
                    future.complete((V) b);
                else
                    nextSteps.forEach(next -> this.apply(next, step, b, future));
            });
        });
    }

    protected abstract <A, B> Optional<CompletableFuture<B>> step(Step<A, B> step, Step<?, A> from, A input);

    protected Step<T, ?> firstStep() {
        return this.first;
    }

    protected Collection<Transition<?, ?>> transitions(Step<?, ?> step) {
        return this.transitions.get(step);
    }

}
