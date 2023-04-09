package com.koralix.stepfn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A {@link Step} is a {@link Function} that is part of a {@link StepFunction}.
 *
 * @param <T> the type of the input to the step
 * @param <R> the type of the result of the step
 * @see StepFunction
 * @author JohanVonElectrum
 * @since 1.0.0
 */
public abstract class Step<T, R> implements Function<T, R> {

    /**
     * The aggregation of the inputs from the previous steps.
     */
    protected final Map<Step<?, T>, T> aggregation = new HashMap<>();

    /**
     * The input from the step function.
     * <p>
     * This is only used for the first step.
     */
    protected T stepFunctionInput;

    /**
     * Aggregates the given input from the given step.
     *
     * @param from the step that produced the input
     * @param input the input
     */
    public void aggregate(Step<?, T> from, T input) {
        if (from == null)
            this.stepFunctionInput = input;
        else
            this.aggregation.put(from, input);
    }

    /**
     * Checks if the step has all the required inputs to be executed.
     *
     * @return true if the step is complete, false otherwise
     */
    public abstract boolean isComplete();

}
