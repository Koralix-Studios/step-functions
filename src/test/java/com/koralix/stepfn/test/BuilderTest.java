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
        SyncStepFunction<String, Boolean> syncStepFunction = StepFunctionBuilder.step(String::length)
                .transition(
                        step -> true,
                        StepFunctionBuilder.step(input -> input > 5)
                ).sync();

        Assertions.assertTrue(syncStepFunction.apply("Hello World"));
        Assertions.assertFalse(syncStepFunction.apply("Hello"));
    }

    @Test
    public void async() {
        AsyncStepFunction<String, Boolean> asyncStepFunction = StepFunctionBuilder.step(String::length)
                .transition(
                        step -> true,
                        StepFunctionBuilder.step(input -> input > 5)
                ).async(() -> Executors.newFixedThreadPool(8));

        Assertions.assertTrue(asyncStepFunction.apply("Hello World").join());
        Assertions.assertFalse(asyncStepFunction.apply("Hello").join());
    }

    @Test
    public void aggregation() {
        StepFunctionBuilder<Integer, Integer> lastStep = StepFunctionBuilder.step(
                aggregation -> aggregation.size() == 2,
                (input, aggregation) -> aggregation.values().stream().mapToInt(Integer::intValue).sum()
        );

        SyncStepFunction<String, Integer> syncStepFunction = StepFunctionBuilder.step(String::length)
                .transition(
                        step -> true,
                        StepFunctionBuilder.<Integer, Integer>step(
                                input -> input + 1
                        ).transition(
                                step -> true,
                                lastStep
                        )
                ).transition(
                        step -> true,
                        StepFunctionBuilder.<Integer, Integer>step(
                                input -> input + 1
                        ).transition(
                                step -> true,
                                lastStep
                        )
                ).sync();

        Assertions.assertEquals(24, syncStepFunction.apply("Hello World"));
        Assertions.assertEquals(12, syncStepFunction.apply("Hello"));
    }

}
