package com.koralix.stepfn;

/**
 * A {@link Transition} provides a way to transition from one {@link Step} to another when certain conditions are met.
 *
 * @param <T> the type of the output of the step that the transition is from
 * @param <R> the type of the input of the step that the transition is to
 * @see StepFunction
 * @author JohanVonElectrum
 * @since 1.0.0
 */
public interface Transition<T, R> {

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
    Step<R, ?> get();

    /**
     * Maps the input to a new input for the next {@link Step}.
     *
     * @param input the input to map
     * @return the mapped input
     */
    R map(T input);

}
