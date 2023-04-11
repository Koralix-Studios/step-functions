package com.koralix.stepfn.test;

import com.koralix.stepfn.AsyncStepFunction;
import com.koralix.stepfn.SyncStepFunction;
import com.koralix.stepfn.builder.StepFunctionBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;

public class BuilderTest {

    @Test
    public void sync() {
        SyncStepFunction<String, Boolean> syncStepFunction = StepFunctionBuilder.step(
                step -> true,
                String::length
        ).transition(
                step -> true,
                StepFunctionBuilder.<Integer, Boolean>step(
                        step -> true,
                        input -> input > 5
                )
        ).sync();

        Assertions.assertTrue(syncStepFunction.apply("Hello World"));
        Assertions.assertFalse(syncStepFunction.apply("Hello"));
    }

    @Test
    public void async() {
        AsyncStepFunction<String, Boolean> asyncStepFunction = StepFunctionBuilder.step(
                step -> true,
                String::length
        ).transition(
                step -> true,
                StepFunctionBuilder.<Integer, Boolean>step(
                        step -> true,
                        input -> input > 5
                )
        ).async(() -> Executors.newFixedThreadPool(8));

        Assertions.assertTrue(asyncStepFunction.apply("Hello World").join());
        Assertions.assertFalse(asyncStepFunction.apply("Hello").join());
    }

}
