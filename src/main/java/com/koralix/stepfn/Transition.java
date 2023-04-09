package com.koralix.stepfn;

import java.util.function.Function;

/**
 * A {@link Transition} provides a way to transition from one {@link Step} to another when certain conditions are met.
 *
 * @param <T> the type of the result of the step that the transition is from
 * @see StepFunction
 * @author JohanVonElectrum
 * @since 1.0.0
 */
public interface Transition<T> {

    /**
     * Checks if the transition is applicable to the given input.
     *
     * @param input the input to check
     * @return true if the transition is applicable, false otherwise
     */
    boolean isApplicable(T input);

    /**
     * Returns the next {@link Step} to transition to.
     *
     * @return the next {@link Step}
     */
    Step<T, ?> get();

}
