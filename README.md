# 📚 Step Functions Library
![Stars Count](https://img.shields.io/github/stars/koralix-studios/step-functions)
![Issues Count](https://img.shields.io/github/issues/koralix-studios/step-functions)
![PR Count](https://img.shields.io/github/issues-pr/koralix-studios/step-functions)
![Build Status](https://img.shields.io/github/actions/workflow/status/koralix-studios/step-functions/build.yml)
![Latest Release](https://img.shields.io/github/v/release/koralix-studios/step-functions)

The Step Functions library is a powerful and flexible tool for creating multiple-step functions.
It provides users with the ability to create both synchronous and asynchronous step functions with ease.
The library offers flexibility in defining custom steps and transitions, allowing for the creation of complex workflows with capabilities such as parallel branching, conditional branching, and branch merging with aggregation.

## 🛠️ How to Use
For a more complete and up-to-date Getting Started guide, please visit our [wiki home page](https://github.com/koralix-studios/step-functions/wiki). 😊

### 💻 Installation
To install the **Step Functions** library, add the following dependency to your project's build file:

```kotlin
dependencies {
  implementation("com.koralix.stepfn:step-functions:1.1.1")
}
```
Make sure to also add the GitHub packages repository to your build file:
```kotlin
repositories {
  maven {
    url = uri("https://maven.pkg.github.com/koralix-studios/step-functions")
    credentials {
      username = project.findProperty("gpr.user") as String?
      password = project.findProperty("gpr.key") as String?
    }
  }
}
```
Don't forget to add the following properties to your `gradle.properties` file:
```properties
gpr.user=your_github_username
gpr.key=your_github_token
```

### 📝 Creating a StepFunction
To create a `StepFunction`, you can use either the `SyncStepFunction` or `AsyncStepFunction` class.
Here’s an example of how to create a `SyncStepFunction`:

```java
Step<String, Integer> initialStep = new Step<>() {
    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public Integer apply(String input) {
        return input.length();
    }
};

SyncStepFunction<String, Boolean> stepFunction = new SyncStepFunction<>(initialStep);
```
And here’s an example of how to create an `AsyncStepFunction`:
```java
Executor executor = Executors.newFixedThreadPool(4);

AsyncStepFunction<String, Boolean> stepFunction = new AsyncStepFunction<>(initialStep, executor);
```

### 🌳 Defining Steps and Branches
You can define additional steps and branches using the `addTransition` method.
Here’s an example of how to add a transition from one step to another:

```java
Step<Integer, Boolean> nextStep = new Step<>() {
    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public Boolean apply(Integer input) {
        return input % 2 == 0;
    }
};

stepFunction.addTransition(initialStep, nextStep, input -> input > 5);
```
In this example, we added a transition from the `initialStep` to the `nextStep`.
The transition will only be applied if the output of the `initialStep` is greater than 5.

### ▶️ Executing a StepFunction
To execute a `StepFunction`, you can use the `apply` method.
Here’s an example of how to execute a `SyncStepFunction`:

```java
boolean result = stepFunction.apply("Hello World!");
```
And here’s an example of how to execute an `AsyncStepFunction`:
```java
CompletableFuture<Boolean> futureResult = stepFunction.apply("Hello World!");
futureResult.thenAccept(result -> {
  // some stuff
});
// or
boolean result = futureResult.join();
```

### 🔀 Merging Branches
When one step transitions to multiple steps with the same output, all the states are executed.
Multiple execution branches can be merged into one using aggregation.
When the library tries to execute a step, it first checks if it is completed. If it is not, the execution is not submitted.
When a `Step` is applied multiple times from multiple branches, the result is aggregated in an internal variable.
When the `isComplete` method returns true, this data can be used to compute an aggregated result in this step based on the multiple branches that called this step.

### ⏹️ Termination
The termination condition for a `StepFunction` is reached when any step executes and does not have any valid transition to another step.
In this case, the output of that step will be the final result of the entire `StepFunction`.
It is important to note that all steps that can output a final result must return the same type.

### ❗ Possible Problems
Using the `addTransition` method during execution on an `AsyncStepFunction` may produce errors because thread safety is not guaranteed.
Make sure to only add transitions before executing the `AsyncStepFunction`.

## 👥 How to Contribute ![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)
Read the [CONTRIBUTING](.github/CONTRIBUTING.md) file for more information.

## 📜 License ![License](https://img.shields.io/github/license/koralix-studios/step-functions)
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.