# JBehave Configuration

This document describes the JBehave setup for behavior-driven development (BDD) testing in the Mealkit project.

## Overview

JBehave is configured to read story files in **Markdown (.md) format** from the `src/test/resources/spec` directory. Stories are executed using Spring Boot Test integration with Testcontainers for database testing.

## Directory Structure

```
src/test/
├── java/
│   └── com/urgoringo/mealkit/jbehave/
│       ├── StoriesRunner.java           # Main JBehave test runner
│       └── steps/                       # Step definition classes
│           └── ExampleSteps.java        # Example step definitions
└── resources/
    └── spec/                            # Story files go here
        └── example.md                   # Example story in Markdown
```

## Running Stories

### Run All Stories

```bash
./gradlew test --tests StoriesRunner
```

This will execute all `.md` files found in `src/test/resources/spec/` and its subdirectories.

### View Reports

After running stories, JBehave generates reports in the `target/jbehave/` directory:

- **HTML Reports**: `target/jbehave/view/index.html` - Main report with all stories
- **Console Output**: Visible in test execution output
- **XML Reports**: `target/jbehave/*.xml` - For CI/CD integration

## Writing Stories

### Story Format

Stories are written in Markdown format with JBehave scenario syntax:

```markdown
# Story Title

Story description goes here.

## Scenario: Scenario name

Given [precondition]
When [action]
Then [expected result]

## Scenario: Another scenario

Given [another precondition]
When [another action]
Then [another expected result]
```

### Example Story

See `src/test/resources/spec/example.md`:

```markdown
# Example Story

This is an example JBehave story written in Markdown format.

## Scenario: Basic application startup

Given the Mealkit application is running
When I check the application status
Then the application should be healthy
```

## Writing Step Definitions

Step definitions are Spring components located in `src/test/java/com/urgoringo/mealkit/jbehave/steps/`.

### Example Step Definition

```java
package com.urgoringo.mealkit.jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.stereotype.Component;

@Component
public class MySteps {

    @Given("the Mealkit application is running")
    public void givenApplicationIsRunning() {
        // Setup code
    }

    @When("I check the application status")
    public void whenICheckStatus() {
        // Action code
    }

    @Then("the application should be healthy")
    public void thenApplicationShouldBeHealthy() {
        // Assertion code
    }
}
```

### Step Definition Guidelines

1. **Annotate with `@Component`**: All step classes must be Spring components to be detected by the SpringStepsFactory
2. **Use JBehave annotations**: `@Given`, `@When`, `@Then`, `@Before`, `@After`
3. **Inject dependencies**: Use `@Autowired` to inject services, repositories, or other beans
4. **Keep steps focused**: Each step should do one thing clearly

## Spring Integration

### Test Configuration

The `StoriesRunner` class integrates JBehave with Spring Boot:

- **@SpringBootTest**: Starts full Spring application context
- **@ActiveProfiles("test")**: Uses test profile
- **@Import(TestContainersConfiguration.class)**: Enables Testcontainers for PostgreSQL

### Available Components

Step definitions have access to all Spring beans:

```java
@Component
public class RecipeSteps {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeService recipeService;

    @Given("a recipe exists with name $name")
    public void givenRecipeExists(String name) {
        // Use injected beans
        recipeRepository.save(new Recipe(name));
    }
}
```

## Best Practices

1. **Organize stories by feature**: Create subdirectories under `spec/` for different features
   ```
   spec/
   ├── recipes/
   │   ├── create_recipe.md
   │   └── update_recipe.md
   └── users/
       └── authentication.md
   ```

2. **Keep scenarios independent**: Each scenario should set up its own test data

3. **Use meaningful names**: Scenario names should clearly describe the behavior being tested

4. **Reuse step definitions**: Write generic steps that can be reused across multiple stories

5. **Clean up test data**: Use `@After` hooks to clean up data after scenarios

## Dependencies

JBehave dependencies are configured in `build.gradle.kts`:

```kotlin
testImplementation("org.jbehave:jbehave-core:5.2.0")
testImplementation("org.jbehave:jbehave-spring:5.2.0")
```

## Troubleshooting

### Stories not found

If stories are not being executed:
- Verify `.md` files are in `src/test/resources/spec/`
- Check that files are included in the build (`./gradlew processTestResources`)
- Look for "Found X story files" in test output

### Steps not matching

If JBehave can't find step definitions:
- Ensure step classes are annotated with `@Component`
- Verify the step pattern matches exactly (including case and spacing)
- Check that the step class is in a package scanned by Spring

### Database issues

If database tests fail:
- Ensure Docker is running (required for Testcontainers)
- Check that `@Import(TestContainersConfiguration.class)` is present on the test class
- Verify Flyway migrations are up to date
