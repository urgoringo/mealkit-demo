# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mealkit is a Spring Boot 3.5 application for managing meal kit recipes. It uses:
- **Java 25** (with toolchain configured in build.gradle.kts)
- **Spring Boot 3.5.7** with Spring Data JPA and Spring Web
- **PostgreSQL** database with Flyway migrations
- **Testcontainers** for integration testing with PostgreSQL
- **Lombok** for reducing boilerplate code
- **MapStruct** for mapping between persistence and domain models
- **JSpecify** for null safety annotations
- **Error Prone** with **NullAway** for compile-time null safety checking

## Build Commands

### Building the Application
```bash
./gradlew build          # Full build with tests
./gradlew assemble       # Build without running tests
./gradlew clean build    # Clean build from scratch
```

### Running Tests
```bash
./gradlew test                                    # Run all tests
./gradlew test --tests ClassName                  # Run specific test class
./gradlew test --tests ClassName.methodName       # Run specific test method
```

**Note**: Tests use Testcontainers with PostgreSQL 17-alpine. Container reuse is enabled in `TestContainersConfiguration` to speed up test execution.

### Running the Application
```bash
./gradlew bootRun        # Run the Spring Boot application
```

### Other Useful Commands
```bash
./gradlew bootJar        # Create executable JAR
./gradlew bootBuildImage # Build OCI/Docker image
./gradlew dependencies   # View dependency tree
./gradlew javadoc        # Generate API documentation
```

## Architecture

### Domain-Driven Design Principles

This project follows **Domain-Driven Design (DDD)** principles:

- **Application Services**: Each use case has a separate application service that executes only that one use case
  - One service per use case (e.g., `CreateRecipeService`, `UpdateRecipeService`)
  - Application services orchestrate the use case flow
  - Keep application services focused on a single responsibility

- **Domain Services**: Only used when business logic does not naturally fit within a single entity
  - Avoid creating domain services unless the logic spans multiple entities
  - Most business logic should reside in domain entities themselves

- **Domain Validation**: Domain models enforce their own invariants
  - Use factory methods (e.g., `create()`) to validate business rules
  - Throw `ValidationException` for business rule violations
  - Domain models should make invalid states impossible to represent
  - Example: `Order.create()` validates minimum recipe count

### Validation and Error Handling

The project uses a consistent approach to validation and error handling:

- **ValidationException**: Custom exception for business rule violations
  - Always includes a non-null error message (enforced via `Objects.requireNonNull`)
  - Overrides `getMessage()` to return guaranteed non-null value
  - Used by domain models to enforce invariants

- **GlobalExceptionHandler**: Centralized exception handling with `@ControllerAdvice`
  - Maps `ValidationException` to HTTP 422 (Unprocessable Entity)
  - Returns consistent error response format
  - Uses exhaustive switch expressions for type safety

**Example validation in domain model:**
```java
@NullMarked
public record Order(Id<Order> id, List<Id<Recipe>> recipeIds) {
    private static final int MINIMUM_RECIPE_COUNT = 3;

    public static Order create(List<Id<Recipe>> recipeIds) {
        if (recipeIds.size() < MINIMUM_RECIPE_COUNT) {
            throw new ValidationException("Order must contain at least " + MINIMUM_RECIPE_COUNT + " recipes");
        }
        return new Order(Id.unassigned(), recipeIds);
    }
}
```

### Persistence and Domain Model Separation

The project maintains strict separation between persistence and domain concerns:

- **Persistence Models**: Classes with JPA annotations for database mapping
  - Use `@Entity`, `@Table`, `@Id`, `@Column`, etc.
  - Located in persistence-specific packages
  - Focused solely on object-relational mapping
  - Make it easy to map between objects and database records
  - May contain JPA-specific fields like `@Version` for optimistic locking

- **Domain Models**: Business logic classes without JPA dependencies
  - Contains actual business rules and domain logic
  - No JPA annotations or JPA-specific fields
  - Pure Java objects focused on business behavior
  - Rich domain models with methods that enforce business invariants

- **Mapping with MapStruct**: Use MapStruct for converting between persistence and domain models
  - Create mapper interfaces with `@Mapper(componentModel = "spring")`
  - MapStruct generates implementation at compile time
  - Keeps mapping logic declarative and type-safe
  - Mappers are Spring beans automatically injected where needed

### Id Wrapper Type Pattern

The project uses a type-safe `Id<T>` wrapper for entity identifiers to handle the null safety challenge of unassigned IDs:

- **Type Safety**: `Id<Recipe>` vs `Id<Customer>` prevents mixing IDs from different entities
- **Null Safety**: Uses `@NullUnmarked` to safely handle unassigned IDs for new entities
- **Domain Language**: Uses `unassigned()` and `isAssigned()` instead of persistence-centric terms

**Creating entities with IDs:**
```java
@NullMarked
public record Recipe(
    Id<Recipe> id,
    String title
) {
    public static Recipe create(String title) {
        return new Recipe(Id.unassigned(), title);
    }
}
```

**Id wrapper implementation pattern:**
```java
@NullMarked
public record Id<T>(Long value) {

    @NullUnmarked  // Opt-out of null checking for this factory method
    public static <T> Id<T> unassigned() {
        return new Id<>(null);
    }

    public static <T> Id<T> of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Use unassigned() for IDs without a value");
        }
        return new Id<>(value);
    }

    public boolean isAssigned() {
        return value != null;
    }
}
```

**Mapping IDs in MapStruct:**
```java
@Mapper(componentModel = "spring")
public interface RecipeMapper {

    default Id<Recipe> mapId(@Nullable Long id) {
        return id == null ? Id.unassigned() : Id.of(id);
    }

    default Long mapId(Id<Recipe> id) {
        return id.value();
    }
}
```

### Database Layer
- **Flyway** manages database migrations in `src/main/resources/db/migration/`
- Migration naming: `V{version}__{description}.sql` (e.g., `V1__create_recipes_table.sql`)
- Hibernate is configured with `ddl-auto: validate` - schema changes MUST be done via Flyway migrations
- Database baseline is automatically created on first migration (`baseline-on-migrate: true`)

### Testing Architecture
- All integration tests should import `TestContainersConfiguration` to get a PostgreSQL container
- Use `@ActiveProfiles("test")` for test-specific configuration
- PostgreSQL container uses `withReuse(true)` to persist between test runs for faster execution
- Container image: `postgres:17-alpine`

### Application Structure
- Main package: `com.urgoringo.mealkit`
- Spring Boot application entry point: `MealkitApplication.java`
- Configuration: `src/main/resources/application.yaml`
- Static resources: `src/main/resources/static/`
- Templates: `src/main/resources/templates/`

## Static Analysis and Null Safety

### Error Prone with NullAway and JSpecify

This project uses **Error Prone** for compile-time static analysis and **NullAway** for null safety checking with **JSpecify** annotations.

#### What are these tools?

- **Error Prone**: Google's static analysis tool that catches common Java mistakes at compile-time
- **NullAway**: Uber's fast null-safety checker that runs as an Error Prone plugin
- **JSpecify**: Industry-standard null safety annotations (v1.0.0)

#### How they work together

1. **Error Prone** runs during compilation and checks for common mistakes
2. **NullAway** (as an Error Prone plugin) analyzes nullability based on JSpecify annotations
3. **JSpecify annotations** mark which values can be null vs. must be non-null

#### Configuration

**Gradle Plugins:**
- `net.ltgt.errorprone` version 4.3.0
- `net.ltgt.nullaway` version 2.3.0

**Dependencies:**
- Error Prone Core: 2.44.0
- NullAway: 0.12.12
- JSpecify: 1.0.0

**JSpecify Mode:** Enabled for full nullness checking on arrays, generics, and type parameters

**Excluded from analysis:**
- Generated code in `build/generated/` (Lombok and MapStruct)
- Test code (Error Prone disabled for tests)

#### Using JSpecify Annotations

**@NullMarked** - Mark a class/package as null-safe by default:
```java
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RecipeService {
    // All parameters and returns are non-null by default
    public Recipe getRecipe(Long id) { ... }
}
```

**@Nullable** - Mark specific nullable elements:
```java
import org.jspecify.annotations.Nullable;

@NullMarked
public class RecipeService {
    // Return type can be null
    public @Nullable Recipe findRecipe(Long id) { ... }

    // Parameter can be null
    public void updateDescription(@Nullable String description) { ... }
}
```

**@NullUnmarked** - Opt-out of null checking for specific scopes:
```java
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public class LegacyCode {
    // Null checking disabled for this class
}
```

#### Build Behavior

- **Compilation fails** if NullAway detects potential null pointer exceptions
- Error messages show exactly where null safety violations occur
- Fix violations by:
  1. Adding `@Nullable` annotations where values can be null
  2. Adding null checks before dereferencing potentially null values
  3. Using Optional for optional values

#### Disabling for specific code

If you need to temporarily disable checks for specific methods:

```java
@SuppressWarnings("NullAway")
public void legacyMethod() {
    // NullAway disabled for this method
}
```

#### Common Patterns

**Null checks:**
```java
public void process(@Nullable String value) {
    if (value != null) {
        // Safe to use value here
        value.length();
    }
}
```

**Method contracts:**
```java
public String requireNonNull(String value) {
    Objects.requireNonNull(value, "value must not be null");
    return value;
}
```

**Spring nullability:**
Spring Framework 6+ uses JSpecify annotations internally. Spring's `@NonNullApi` and `@Nullable` are compatible with JSpecify mode.

#### Compatibility Notes

**Lombok:**
- Configured to add `@Generated` annotations via `lombok.config`
- Generated code is excluded from analysis
- NullAway provides best-effort compatibility with Lombok

**MapStruct:**
- Generated mappers are excluded from analysis
- MapStruct-generated code excluded via `excludedPaths`
- Manually annotate mapper interfaces with `@Nullable` as needed

**Java 25:**
- NullAway JSpecify mode works optimally on Java 25
- No additional compiler flags needed

#### Troubleshooting

**Build fails with NullAway errors:**
1. Read the error message - it shows the exact line and issue
2. Add `@Nullable` if the value can legitimately be null
3. Add null checks if you're dereferencing a nullable value
4. Ensure Spring beans that can't be null are properly initialized

**False positives:**
- Consider if the error is actually catching a real bug
- If truly a false positive, use `@SuppressWarnings("NullAway")` sparingly
- Report false positives to the NullAway project

**Generated code errors:**
- Verify `lombok.config` exists with `lombok.addLombokGeneratedAnnotation = true`
- Check that excluded paths pattern matches generated code location
- Default exclusion: `.*/build/generated/.*`

## Development Workflow

### Adding Database Changes
1. Create new Flyway migration in `src/main/resources/db/migration/`
2. Use sequential versioning: V1, V2, V3, etc.
3. Run tests to validate migration against PostgreSQL container
4. Never modify existing migrations once committed

### Testing Guidelines

#### Integration Testing
- Integration tests require `@Import(TestContainersConfiguration.class)`
- Tests automatically use Testcontainers PostgreSQL instance
- No need for manual database setup during testing

#### JBehave Test Architecture

The project uses JBehave for behavior-driven development (BDD) testing:

- **Self-Contained Tests**: Tests create their own test data via the REST API
  - Makes tests independent and easier to understand
  - Avoids relying on database migrations or shared test data
  - Each scenario starts with a clean database state

- **ApplicationRunner Pattern**: Encapsulates API interactions
  - Provides high-level methods for interacting with the application API
  - Hides low-level HTTP details from step definitions
  - Returns `ApiResponse<T>` for uniform error handling

- **ApiResponse Pattern**: Type-safe response handling using sealed interfaces
  - `ApiResponse.Success<T>` - successful response with body
  - `ApiResponse.Error<T>` - error response with status code and body
  - Use `expectSuccess()` when success is expected
  - Use `expectError()` when error is expected
  - Exhaustive pattern matching ensures all cases are handled

**Example ApplicationRunner method:**
```java
public ApiResponse<SubscriptionResponse> createSubscription(String customerEmail, List<Long> recipeIds) {
    CreateSubscriptionRequest request = new CreateSubscriptionRequest(customerEmail, recipeIds);
    ResponseEntity<SubscriptionResponse> response = restTemplate.postForEntity(
            "/subscriptions", request, SubscriptionResponse.class);
    return ApiResponse.from(response);
}
```

**Example step definition usage:**
```java
@When("customer chooses these recipes for upcoming order")
public void whenCustomerChoosesRecipes() {
    response = app.createSubscription(customerEmail, chosenRecipeIds);
    subscription = response.expectSuccess();
}

@Then("system returns $statusCode with validation error")
public void thenSystemReturnsStatusWithValidationError(int statusCode) {
    int actualStatusCode = response.expectError();
    assertEquals(statusCode, actualStatusCode);
}
```

#### Test Data Management
- Use `ApplicationRunner` methods to create test data via API
- Avoid using repositories or services directly in test steps
- Clean up data in `@BeforeScenario` hooks
- Delete in proper order to respect foreign key constraints (e.g., subscriptions before recipes)
