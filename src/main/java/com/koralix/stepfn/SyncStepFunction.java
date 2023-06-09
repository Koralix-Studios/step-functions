package com.koralix.stepfn;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link StepFunction} that computes its result synchronously.
 * <p>
 * Check the {@link StepFunction} documentation for more information and examples.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see StepFunction
 * @see Step
 * @see Transition
 * @author JohanVonElectrum
 * @since 1.0.0
 */
public class SyncStepFunction<T, R> extends StepFunction<T, R, R> {

    /**
     * Creates a new {@link SyncStepFunction} with the given initial step and transitions.
     *
     * @param initialStep the initial step
     * @param transitions the transitions
     * @deprecated use {@link #SyncStepFunction(Step)} instead - this constructor will be removed in 1.2.0
     * @since 1.0.0
     */
    @Deprecated
    public SyncStepFunction(Step<T, ?> initialStep, Map<Step<?, ?>, Set<Transition<?, ?>>> transitions) {
        super(initialStep, transitions);
    }

    /**
     * Creates a new {@link SyncStepFunction} with the given initial step.
     *
     * @param initialStep the initial step
     * @since 1.0.0
     */
    public SyncStepFunction(Step<T, ?> initialStep) {
        super(initialStep);
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @since 1.0.0
     */
    @Override
    public R apply(T t) {
        CompletableFuture<R> future = new CompletableFuture<>();
        this.apply(this.firstStep(), null, t, future);
        return future.join();
    }

    /**
     * Applies the given {@link Step} to the given input.
     * <p>
     * If the {@link Step} is complete, the result is returned in a completed {@link CompletableFuture}.
     * <p>
     * If the step is not complete, an empty {@link Optional} is returned and the input is aggregated.
     *
     * @param step  the step to apply
     * @param from  the step from which the input was received
     * @param input the input to the step
     * @param <A>   the input type of the step
     * @param <B>   the output type of the step
     * @return the result of the step in a completed {@link CompletableFuture} if the step is complete,
     *         an empty {@link Optional} otherwise
     * @since 1.0.0
     */
    @Override
    protected <A, B> Optional<CompletableFuture<B>> step(Step<A, B> step, Step<?, ?> from, A input) {
        step.aggregate(from, input);
        if (step.isComplete())
            return Optional.of(CompletableFuture.completedFuture(step.apply(input)));
        else
            return Optional.empty();
    }
}
