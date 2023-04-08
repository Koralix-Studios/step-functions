package com.koralix.stepfn.test;

import com.koralix.stepfn.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class AsyncTest {

    @Test
    public void singleStep() {
        Step<String, String> step = new Step<>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public String apply(String s) {
                return s;
            }
        };
        StepFunction<String, String, CompletableFuture<String>> stepFunction = new AsyncStepFunction<>(
                step,
                Map.ofEntries(),
                Executors.newFixedThreadPool(8)
        );
        CompletableFuture<String> future = stepFunction.apply("test");
        future.thenAccept(s -> Assertions.assertEquals("test", s));
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
        StepFunction<String, String, CompletableFuture<String>> stepFunction = new AsyncStepFunction<>(
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
                ),
                Executors.newFixedThreadPool(8)
        );
        CompletableFuture<String> future = stepFunction.apply("test");
        future.thenAccept(s -> Assertions.assertEquals("TEST_", s));
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
        StepFunction<String, String, CompletableFuture<String>> stepFunction = new AsyncStepFunction<>(
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
                ),
                Executors.newFixedThreadPool(8)
        );
        CompletableFuture<String> alice = stepFunction.apply("Hello");
        alice.thenAccept(s -> Assertions.assertEquals("Hello Alice!", s));
        CompletableFuture<String> bob = stepFunction.apply("Bye");
        bob.thenAccept(s -> Assertions.assertEquals("Bye Bob!", s));
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
        StepFunction<String, Collection<String>, CompletableFuture<Collection<String>>> stepFunction = new AsyncStepFunction<>(
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
                ),
                Executors.newFixedThreadPool(8)
        );
        CompletableFuture<Collection<String>> result = stepFunction.apply("Hello");
        result.thenAccept(s -> Assertions.assertEquals(Set.of("Hello Alice", "Hello Bob"), s));
    }

}
