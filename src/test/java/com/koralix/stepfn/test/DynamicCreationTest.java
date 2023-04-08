package com.koralix.stepfn.test;

import com.koralix.stepfn.Step;
import com.koralix.stepfn.StepFunction;
import com.koralix.stepfn.SyncStepFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DynamicCreationTest {

    @Test
    public void test() {
        Step<String, String> step1 = new Step<String, String>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public String apply(String s) {
                return s;
            }
        };
        Step<String, String> step2 = new Step<String, String>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public String apply(String s) {
                return s.toUpperCase();
            }
        };
        StepFunction<String, String, String> stepFunction = new SyncStepFunction<>(step1);
        stepFunction.addTransition(step1, step2, s -> true);
        String result = stepFunction.apply("test");
        Assertions.assertEquals("TEST", result);
    }

}
