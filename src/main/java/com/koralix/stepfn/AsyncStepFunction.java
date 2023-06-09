package com.koralix.stepfn;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * A {@link StepFunction} that returns a {@link CompletableFuture}.
 * <p>
 * The function is applied asynchronously using the {@link Executor} provided to the constructor.
 * <p>
 * Check the {@link StepFunction} documentation for more information and examples.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see StepFunction
 * @see Step
 * @see Transition
 * @see CompletableFuture
 * @author JohanVonElectrum
 * @since 1.0.0
 */
public class AsyncStepFunction<T, R> extends StepFunction<T, R, CompletableFuture<R>> {

    private final Supplier<ExecutorService> executorSupplier;
    private ExecutorService executor;

    /**
     * Creates a new {@link AsyncStepFunction} with the given initial step and transitions.
     *
     * @param initialStep      the initial step
     * @param transitions      the transitions
     * @param executorSupplier the supplier of the executor to use for asynchronous computation
     * @deprecated use {@link #AsyncStepFunction(Step, Supplier)} instead - this constructor will be removed in 1.2.0
     * @since 1.0.0
     */
    @Deprecated
    public AsyncStepFunction(
            Step<T, ?> initialStep,
            Map<Step<?, ?>, Set<Transition<?, ?>>> transitions,
            Supplier<ExecutorService> executorSupplier
    ) {
        super(initialStep, transitions);
        this.executorSupplier = executorSupplier;
    }

    /**
     * Creates a new {@link AsyncStepFunction} with the given initial step.
     *
     * @param initialStep      the initial step
     * @param executorSupplier the supplier of the executor to use for asynchronous computation
     * @since 1.0.0
     */
    public AsyncStepFunction(Step<T, ?> initialStep, Supplier<ExecutorService> executorSupplier) {
        super(initialStep);
        this.executorSupplier = executorSupplier;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result in a {@link CompletableFuture}
     * @since 1.0.0
     */
    @Override
    public CompletableFuture<R> apply(T t) {
        CompletableFuture<R> future = new CompletableFuture<>();
        this.executor = this.executorSupplier.get();
        this.apply(this.firstStep(), null, t, future);
        future.whenComplete((r, e) -> {
            this.executor.shutdownNow();
        });
        return future;
    }

    /**
     * Applies the given {@link Step} to the given input.
     * <p>
     * If the {@link Step} is complete, the result is returned in a {@link CompletableFuture}.
     * <p>
     * If the step is not complete, an empty {@link Optional} is returned and the input is aggregated.
     *
     * @param step  the step to apply
     * @param from  the step from which the input was received
     * @param input the input to the step
     * @param <A>   the input type of the step
     * @param <B>   the output type of the step
     * @return the result of the step in a {@link CompletableFuture} if the step is complete,
     *         an empty {@link Optional} otherwise
     * @since 1.0.0
     */
    @Override
    protected <A, B> Optional<CompletableFuture<B>> step(Step<A, B> step, Step<?, ?> from, A input) {
        step.aggregate(from, input);
        if (step.isComplete()) {
            try {
                return Optional.of(CompletableFuture.supplyAsync(() -> step.apply(input), this.executor));
            } catch (RejectedExecutionException ignored) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
