package com.koralix.stepfn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class StepFunction<T, V, R> implements Function<T, R> {

    private final Step<T, ?> first;
    private final Map<Step<?, ?>, Set<Transition<?>>> transitions = new HashMap<>();

    public StepFunction(
            Step<T, ?> first,
            Map<Step<?, ?>, Set<Transition<?>>> transitions
    ) {
        this.first = first;
        this.transitions.putAll(transitions.entrySet().stream().map(entry -> Map.entry(
                entry.getKey(),
                new HashSet<>(entry.getValue())
        )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public StepFunction(Step<T, ?> first) {
        this.first = first;
    }

    public <A> void addTransition(Step<?, A> from, Step<A, ?> to, Function<A, Boolean> predicate) {
        this.transitions.computeIfAbsent(from, step -> new HashSet<>()).add(new Transition<A>() {
            @Override
            public boolean isApplicable(A input) {
                return predicate.apply(input);
            }

            @Override
            public Step<A, ?> get() {
                return to;
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected <A, B> void apply(Step<A, B> step, Step<?, A> from, A input, CompletableFuture<V> future) {
        this.step(step, from, input).ifPresent(completableFuture -> {
            step.aggregation.clear();
            step.stepFunctionInput = null;

            completableFuture.thenAccept(b -> {
                Set<Transition<?>> transitions = this.transitions(step);
                if (transitions == null) {
                    future.complete((V) b);
                    return;
                }
                List<? extends Step<B, ?>> nextSteps = transitions.stream()
                        .map(transition -> (Transition<B>) transition)
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

    protected Set<Transition<?>> transitions(Step<?, ?> step) {
        return this.transitions.get(step);
    }

}
