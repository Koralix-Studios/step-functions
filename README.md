# 📚 Step Functions Library
![Stars Count](https://img.shields.io/github/stars/koralix-studios/step-functions)
![Issues Count](https://img.shields.io/github/issues/koralix-studios/step-functions)
![PR Count](https://img.shields.io/github/issues-pr/koralix-studios/step-functions)
![Build Status](https://img.shields.io/github/actions/workflow/status/koralix-studios/step-functions/build.yml)
![Latest Release](https://img.shields.io/github/v/release/koralix-studios/step-functions)

The Step Functions library is a powerful and flexible tool for creating multiple-step functions.
It provides users with the ability to create both synchronous and asynchronous step functions with ease.
The library offers flexibility in defining custom steps and transitions, allowing for the creation of complex workflows with capabilities such as parallel branching, conditional branching, and branch merging with aggregation.

## 💻 Installation
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

## Usage Examples
### 🚀 Basic Usage
Here’s a basic usage example that demonstrates how to create a synchronous step function using the Step Functions library:

```java
SyncStepFunction<String, Boolean> syncStepFunction = StepFunctionBuilder.step(String::length)
        .transition(
                step -> true,
                StepFunctionBuilder.step(input -> input > 5)
        ).sync();

syncStepFunction.apply("Hello World"); // returns true
syncStepFunction.apply("Hello"); // returns false
```
In this example, we create a `SyncStepFunction` that takes a `String` as input and returns a `Boolean` as output.
The function first applies the `String::length` step to compute the length of the input string.
It then transitions to a second step that checks if the length of the input string is greater than 5.
The first call to `apply` returns `true` because the length of “Hello World” is greater than 5, while the second call returns `false` because the length of “Hello” is not greater than 5.

### 🔧 Advanced Usage

Here’s an advanced usage example that demonstrates how to create an asynchronous step function with parallel branching and branch merging using aggregation:

```java
StepFunctionBuilder<Integer, Integer> lastStep = StepFunctionBuilder.step(
        aggregation -> aggregation.size() == 2,
        (input, aggregation) -> aggregation.values().stream().mapToInt(Integer::intValue).sum()
);

AsyncStepFunction<String, Integer> asyncStepFunction = StepFunctionBuilder.step(String::length)
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
        ).async(() -> Executors.newFixedThreadPool(8));

asyncStepFunction.apply("Hello World").join(); // returns 24
asyncStepFunction.apply("Hello").join();       // returns 12
```
In this example, we create an `AsyncStepFunction` that takes a `String` as input and returns an `Integer` as output.
The function first applies the `String::length` step to compute the length of the input string.
It then transitions to two parallel branches that both apply a step that adds 1 to the length of the input string.
These two branches then merge into a final step that aggregates the results from both branches by summing them.
The first call to `apply` returns 24 because “Hello World” has a length of 11 and both branches add 1 to this length before summing the results (11 + 1 + 11 + 1 = 24), while the second call returns 12 because “Hello” has a length of 5 (5 + 1 + 5 + 1 = 12).

### 📝 More Usage Examples
For a more complete and up-to-date guide, please visit our [wiki home page](https://github.com/koralix-studios/step-functions/wiki). 😊

## Known Issues
### Issue 1: Dynamic addition of transitions to an executing asynchronous step function
When a transition is added dynamically to an asynchronous step function while it is executing, the expected execution workflow may be altered.
This can result in exceptions being thrown due to concurrent modifications of the internal transition map.

### Issue 2: Multiple steps with different return types
If multiple steps within a step function have different return types, errors may occur.
It is important to ensure that all terminal steps have the same return type or return a type that extends the output type of the step function.

## 👥 How to Contribute ![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)
Contributions to Step Functions are welcome!
Before contributing, please read our [code of conduct](CODE_OF_CONDUCT.md) and [contributing guidelines](.github/CONTRIBUTING.md).

## 📜 License ![License](https://img.shields.io/github/license/koralix-studios/step-functions)
This library is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for more information.