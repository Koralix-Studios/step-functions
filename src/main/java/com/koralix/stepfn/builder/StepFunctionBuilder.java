package com.koralix.stepfn.builder;

import com.koralix.stepfn.AsyncStepFunction;
import com.koralix.stepfn.Step;
import com.koralix.stepfn.StepFunction;
import com.koralix.stepfn.SyncStepFunction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A builder for {@link StepFunction}s.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see StepFunction
 * @since 1.1.0
 */
public class StepFunctionBuilder<T, R> {

    private final Step<T, R> step;
    private final Set<TransitionData<R, ?>> transitions = new HashSet<>();

    private StepFunctionBuilder(Step<T, R> step) {
        this.step = step;
    }

    /**
     * Creates a new {@link StepFunctionBuilder} with the given initial step.
     *
     * @param isComplete a function that returns {@code true} if the step is complete. Used for aggregating steps.
     * @param apply      the function to apply to the input
     * @param <T>        the type of the input to the function
     * @param <V>        the type of the result of the function
     * @return a new {@link StepFunctionBuilder}
     * @see StepFunction
     * @since 1.1.0
     */
    public static <T, V> StepFunctionBuilder<T, V> step(Function<Step<T, V>, Boolean> isComplete, Function<T, V> apply) {
        return new StepFunctionBuilder<>(new Step<T, V>() {
            @Override
            public V apply(T input) {
                return apply.apply(input);
            }

            @Override
            public boolean isComplete() {
                return isComplete.apply(this);
            }
        });
    }

    private static <T, R> void build(
            StepFunction<?, ?, ?> stepFunction,
            Collection<TransitionData<T, R>> transitions
    ) {
        for (TransitionData<T, R> transition : transitions) {
            stepFunction.addTransition(
                    transition.from,
                    transition.to.step,
                    transition.condition,
                    transition.mapper
            );

            make(stepFunction, transition.to.transitions);
        }
    }

    private static void make(
            StepFunction<?, ?, ?> stepFunction,
            Collection<? extends TransitionData<?, ?>> transitions
    ) {
        build(stepFunction, transitions.stream()
                .map(transitionData -> (TransitionData<?, ?>) transitionData)
                .collect(Collectors.toList()));
    }

    /**
     * Creates a new {@link SyncStepFunction} from this builder.
     *
     * @param <V> the type of the result of the function
     * @return a new {@link SyncStepFunction}
     * @see SyncStepFunction
     * @since 1.1.0
     */
    public <V> SyncStepFunction<T, V> sync() {
        SyncStepFunction<T, V> stepFunction = new SyncStepFunction<>(this.step);
        make(stepFunction, this.transitions);
        return stepFunction;
    }

    /**
     * Creates a new {@link AsyncStepFunction} from this builder.
     *
     * @param executorSupplier the supplier of the executor to use for asynchronous computation
     * @param <V> the type of the result of the function
     * @return a new {@link AsyncStepFunction}
     * @see AsyncStepFunction
     * @since 1.1.0
     */
    public <V> AsyncStepFunction<T, V> async(Supplier<ExecutorService> executorSupplier) {
        AsyncStepFunction<T, V> stepFunction = new AsyncStepFunction<>(this.step, executorSupplier);
        make(stepFunction, this.transitions);
        return stepFunction;
    }

    /**
     * Adds a transition to this builder.
     *
     * @param condition the condition to check
     * @param nextStep  the next step to transition to
     * @return this builder
     * @see StepFunction
     * @since 1.1.0
     */
    public StepFunctionBuilder<T, R> transition(
            Function<R, Boolean> condition,
            StepFunctionBuilder<R, ?> nextStep
    ) {
        this.transitions.add(new TransitionData<>(this.step, nextStep, condition, Function.identity()));
        return this;
    }

    /**
     * Adds a transition to this builder.
     *
     * @param condition the condition to check
     * @param mapper    the mapper to apply to the result of the previous step
     * @param nextStep  the next step to transition to
     * @param <V>       the type of the input of the next step
     * @return this builder
     * @see StepFunction
     * @since 1.1.0
     */
    public <V> StepFunctionBuilder<T, R> transition(
            Function<R, Boolean> condition,
            Function<R, V> mapper,
            StepFunctionBuilder<V, ?> nextStep
    ) {
        this.transitions.add(new TransitionData<>(this.step, nextStep, condition, mapper));
        return this;
    }

    private record TransitionData<T, R>(
            Step<?, T> from,
            StepFunctionBuilder<R, ?> to,
            Function<T, Boolean> condition,
            Function<T, R> mapper
    ) {}

}
