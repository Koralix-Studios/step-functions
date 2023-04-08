package com.koralix.stepfn.test;

import com.koralix.stepfn.Step;
import com.koralix.stepfn.StepFunction;
import com.koralix.stepfn.SyncStepFunction;
import com.koralix.stepfn.Transition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SyncTest {

    @Test
    public void singleStep() {
        StepFunction<String, String, String> stepFunction = new SyncStepFunction<>(
                new Step<String, String>() {
                    @Override
                    public boolean isComplete() {
                        return true;
                    }

                    @Override
                    public String apply(String s) {
                        return s;
                    }
                },
                Map.of()
        );
        String result = stepFunction.apply("test");
        Assertions.assertEquals("test", result);
    }

    @Test
    public void multiStep() {
        Step<String, String> step1 = new Step<>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public String apply(String s) {
                return s;
            }
        };
        Step<String, String> step2 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s.toUpperCase();
            }
        };
        Step<String, String> step3 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s + "_";
            }
        };
        StepFunction<String, String, String> stepFunction = new SyncStepFunction<>(
                step1,
                Map.ofEntries(
                        Map.entry(step1, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step2;
                                    }
                                }
                        )),
                        Map.entry(step2, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step3;
                                    }
                                }
                        ))
                )
        );
        String result = stepFunction.apply("test");
        Assertions.assertEquals("TEST_", result);
    }

    @Test
    public void branch() {
        Step<String, String> step1 = new Step<>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public String apply(String s) {
                return s;
            }
        };
        Step<String, String> step2 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s + " Alice";
            }
        };
        Step<String, String> step3 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s + " Bob";
            }
        };
        Step<String, String> step4 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s + "!";
            }
        };
        StepFunction<String, String, String> stepFunction = new SyncStepFunction<>(
                step1,
                Map.ofEntries(
                        Map.entry(step1, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return input.equals("Hello");
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step2;
                                    }
                                },
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return input.equals("Bye");
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step3;
                                    }
                                }
                        )),
                        Map.entry(step2, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step4;
                                    }
                                }
                        )),
                        Map.entry(step3, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step4;
                                    }
                                }
                        ))
                )
        );
        String alice = stepFunction.apply("Hello");
        Assertions.assertEquals("Hello Alice!", alice);
        String bob = stepFunction.apply("Bye");
        Assertions.assertEquals("Bye Bob!", bob);
    }

    @Test
    public void aggregation() {
        Step<String, String> step1 = new Step<>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public String apply(String s) {
                return s;
            }
        };
        Step<String, String> step2 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s + " Alice";
            }
        };
        Step<String, String> step3 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 1;
            }

            @Override
            public String apply(String s) {
                return s + " Bob";
            }
        };
        Step<String, Collection<String>> step4 = new Step<>() {
            @Override
            public boolean isComplete() {
                return this.aggregation.size() == 2;
            }

            @Override
            public Collection<String> apply(String s) {
                return new ArrayList<>(this.aggregation.values());
            }
        };
        StepFunction<String, Collection<String>, Collection<String>> stepFunction = new SyncStepFunction<>(
                step1,
                Map.ofEntries(
                        Map.entry(step1, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step2;
                                    }
                                },
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, String> get() {
                                        return step3;
                                    }
                                }
                        )),
                        Map.entry(step2, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, ?> get() {
                                        return step4;
                                    }
                                }
                        )),
                        Map.entry(step3, Set.of(
                                new Transition<String>() {
                                    @Override
                                    public boolean isApplicable(String input) {
                                        return true;
                                    }

                                    @Override
                                    public Step<String, ?> get() {
                                        return step4;
                                    }
                                }
                        ))
                )
        );
        Collection<String> result = stepFunction.apply("Hello");
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("Hello Alice"));
        Assertions.assertTrue(result.contains("Hello Bob"));
    }

}
