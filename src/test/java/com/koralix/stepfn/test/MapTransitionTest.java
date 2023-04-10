package com.koralix.stepfn.test;

import com.koralix.stepfn.Step;
import com.koralix.stepfn.StepFunction;
import com.koralix.stepfn.SyncStepFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapTransitionTest {

    @Test
    public void test() {
        Step<String, Integer> step1 = new Step<>() {
            @Override
            public Integer apply(String input) {
                return input.length();
            }

            @Override
            public boolean isComplete() {
                return true;
            }
        };
        Step<Boolean, Integer> step2 = new Step<>() {
            @Override
            public Integer apply(Boolean input) {
                return input ? 1 : 0;
            }

            @Override
            public boolean isComplete() {
                return true;
            }
        };

        StepFunction<String, ?, Integer> lengthCheck = new SyncStepFunction<>(step1);

        lengthCheck.addTransition(
                step1,
                step2,
                input -> true,
                input -> input > 5
        );

        Assertions.assertEquals(1, lengthCheck.apply("Hello World"));
        Assertions.assertEquals(0, lengthCheck.apply("Hello"));
    }

}
