# LLM prompts for contributors
## What is LLM?
A **large language model (LLM)** is a computer program that has been trained to understand and generate human language🗣️.
It does this by analyzing vast amounts of text data📚 and learning the patterns and structures of language.
This allows it to generate text that is similar to what a human might write or say✍️.
LLMs can be used for a variety of tasks such as **translation**, **summarization**, and even writing articles or stories📝.

## What is the purpose of this document?
This document is a collection of prompts for contributors to the **Step Functions** documentation.
The prompts are intended to help contributors write documentation that is clear, concise, and consistent.
The prompts are not intended to be exhaustive.
Contributors should use their best judgment to determine what is appropriate for a given situation.

## Prompts
### Release notes
#### Full length
You can use this prompt to generate a release title and body in markdown format for version `X.X.X` of the Step Functions library.
Replace `X.X.X` with the version number of the new release and `[list of features, improvements or bug fixes]` with the specific features, improvements or bug fixes that are included in the new release.
```markdown
Generate a unique and original release title and body in markdown format for version `X.X.X` of the Step Functions library.
The features, enhancements, and bug fixes included in the release are: [list of features, improvements or bug fixes]

The release title should start with the version number, followed by a dash and a short description of the release using emojis and a catchy phrase.
For example, in version 1.0.0 we had the title "1.0.0 - 🚶‍♂️Step by Step🚶‍♀️", while in version 1.1.0 we had the title "1.1.0 - 🏗️ Building Better Workflows 🔄".
Do not replicate the exact same messages from previous releases.

The release body should begin with an introductory paragraph that announces the release and its version number, using emojis and phrases such as "We're excited to announce" or "Introducing version".
Use bold to highlight the version number and other important keywords throughout the release body.
For example, in version 1.0.0 the introduction was in the style of "🎉🚀 We are excited to announce the release of version **1.0.0** of our library for creating **multiple-step functions**! 🚀🎉", while in version 1.0.1 it was "🚀 Introducing version **1.0.1** of the **Step Functions** library! 🚀".

In the following one or two paragraphs, depending on whether it is a major, minor or patch release, describe the additions and/or improvements included in the release. Use emojis to highlight key features and enhancements and bold to emphasize important keywords. For example, in version 1.0.0 this section had two paragraphs explaining new features such as synchronous and asynchronous step functions and custom steps and transitions because it was a major version with new features, while in version 1.0.1 this section had one paragraph explaining a bug fix for the aggregation clear process in `AsyncStepFunctions` because it was a patch version with a fix.

Include a branding enforcement paragraph that reinforces the library's capabilities and value proposition, using phrases such as "powerful 💪 and flexible 🧘 tool" or "complex workflows made easy". For example, in version 1.1.0 this section was "This enhances the **robustness** 💪 and **resilience** 🧘 of our library for building **multiple-step functions** with ease."

End the release body with the catchphrase "Complex workflows made easy - 🚶‍♂️Step by Step🚶‍♀️ with Step Functions".

Remember to keep the structure and style consistent with previous releases while ensuring that all content is unique and original.
```

#### 2000 characters
You can use this prompts to generate a release title and body in markdown format for version `X.X.X` of the Step Functions library.  
1. Send the first prompt, replacing `X.X.X` with the version number of the new release and `[list of features, improvements or bug fixes]` with the specific features, improvements or bug fixes that are included in the new release.
    ```markdown
    In version X.X.X of the Step Functions library, the following features, improvements or bug fixes have been added: [list of features, improvements or bug fixes].
    Please wait for further instructions on how to use this information to generate a release title and body in markdown format for version X.X.X of the Step Functions library.
    ```
2. After sending the first prompt and receiving a response from the language model, send the second prompt, replacing `X.X.X` with the version number of the new release.
   The language model will use the information provided in the first prompt to generate a release title and body in markdown format for the new release in the same style and structure as previous releases.
    ```markdown
    Generate a unique and original release title and body in markdown format for version X.X.X of the Step Functions library.
    
    The title should start with the version number, followed by a dash and a short description using emojis and a catchy phrase.
    For example, in version 1.0.0 we had the title "1.0.0 - 🚶‍♂️Step by Step🚶‍♀️", while in version 1.1.0 we had the title "1.1.0 - 🏗️ Building Better Workflows 🔄".
    
    The body should begin with an introductory paragraph announcing the release and its version number, using emojis and bold for the version number.
    For example, in version 1.0.0 the introduction was "🎉🚀 We are excited to announce the release of version **1.0.0** of our library for creating **multiple-step functions**! 🚀🎉", while in version 1.0.1 it was "🚀 Introducing version **1.0.1** of the **Step Functions** library! 🚀". This illustrates how a major release may have a more elaborate introduction than a patch release.
    
    In the following one or two paragraphs, describe the additions and/or improvements included in the release using emojis and bold for important keywords.
    If only an improvement or a bug fix has occurred, this section is typically a single paragraph unless there are multiple improvements or bug fixes that require additional explanation.
    For example, in version 1.0.1 this section had one paragraph explaining a bug fix for the aggregation clear process in `AsyncStepFunctions`.
    
    Include a branding enforcement paragraph that reinforces the library's capabilities and value proposition.
    For example, in version 1.1.0 this section was "This enhances the **robustness** 💪 and **resilience** 🧘 of our library for building **multiple-step functions** with ease."
    
    End with the catchphrase "**Complex** workflows made **easy** - 🚶‍♂️Step by Step🚶‍♀️ with **Step Functions**".
    
    Keep the structure and style consistent with previous releases while ensuring all content is unique and original.
    ```
