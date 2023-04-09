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

/**
 * A {@link StepFunction} is a {@link Function} that is composed of {@link Step}s.
 * <p>
 * Example:
 * <pre>
 * {@code
 * Step<String, Integer> step1 = new Step<>() {
 *     @Override
 *     public Integer apply(String input) {
 *         return input.length();
 *     }
 *
 *     @Override
 *     public boolean isComplete() {
 *         return true;
 *     }
 * };
 * Step<Integer, Boolean> step2 = new Step<>() {
 *     @Override
 *     public Integer apply(Integer input) {
 *         return input > 5;
 *     }
 *
 *     @Override
 *     public boolean isComplete() {
 *         return true;
 *     }
 * };
 *
 * StepFunction<String, ?, Boolean> lengthCheck = new SyncStepFunction<>(step1);
 *
 * lengthCheck.addTransition(
 *         step1,
 *         step2,
 *         input -> true
 * );
 *
 * lengthCheck.apply("Hello World"); // true
 * lengthCheck.apply("Hello"); // false
 *
 * }
 * </pre>
 *
 * @param <T> the type of the input to the function
 * @param <V> the type of the internal result of the function
 * @param <R> the type of the external result of the function
 * @see Step
 * @see Transition
 * @author JohanVonElectrum
 * @since 1.0.0
 */
public abstract class StepFunction<T, V, R> implements Function<T, R> {

    private final Step<T, ?> initialStep;
    private final Map<Step<?, ?>, Set<Transition<?>>> transitions = new HashMap<>();

    /**
     * Creates a new {@link StepFunction} with the given initial {@link Step} and {@link Transition}s.
     *
     * @param initialStep the initial step
     * @param transitions the transitions
     */
    public StepFunction(
            Step<T, ?> initialStep,
            Map<Step<?, ?>, Set<Transition<?>>> transitions
    ) {
        this.initialStep = initialStep;
        this.transitions.putAll(transitions.entrySet().stream().map(entry -> Map.entry(
                entry.getKey(),
                new HashSet<>(entry.getValue())
        )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    /**
     * Creates a new {@link StepFunction} with the given initial {@link Step}.
     *
     * @param initialStep the initial step
     */
    public StepFunction(Step<T, ?> initialStep) {
        this.initialStep = initialStep;
    }

    /**
     * Adds a {@link Transition} from the first given {@link Step} to the second given {@link Step}
     * with the given predicate.
     *
     * @param from      the step to transition from
     * @param to        the step to transition to
     * @param predicate the predicate to determine if the transition is applicable
     * @param <A>       the output type of the from step
     */
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

    /**
     * Recursive method that applies the given {@link Step} to the given input.
     * <p>
     * This method implements the logic for aggregating the inputs of the {@link Step}s and applying complete {@link Step}s.
     * <p>
     * After the {@link Step} has been applied, the method will complete the {@link CompletableFuture}
     * with the result of the {@link Step}.
     *
     * @param step the step to apply
     * @param from the step that the input is coming from
     * @param input the input
     * @param future the future to complete
     * @param <A> the input type of the step
     * @param <B> the output type of the step
     */
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

    /**
     * Computes the result of the given {@link Step} with the given input.
     * <p>
     * If the given {@link Step} is not complete the input is stored in the {@link Step} for aggregation
     * and the result is empty.
     *
     * @param step  the step to execute
     * @param from  the step that the input is coming from
     * @param input the input
     * @param <A>   the input type of the step
     * @param <B>   the output type of the step
     * @return the result of the step
     */
    protected abstract <A, B> Optional<CompletableFuture<B>> step(Step<A, B> step, Step<?, A> from, A input);

    /**
     * Returns the initial {@link Step} of this {@link StepFunction}.
     *
     * @return the initial step
     */
    protected Step<T, ?> firstStep() {
        return this.initialStep;
    }

    /**
     * Returns the {@link Transition}s that transition from the given {@link Step}.
     *
     * @param step the step
     * @return the transitions
     */
    protected Set<Transition<?>> transitions(Step<?, ?> step) {
        return this.transitions.get(step);
    }

}
