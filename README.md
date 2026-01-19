# Mealkit

![Java CI with Gradle](https://github.com/urgoringo/jbehave-demo/workflows/Java%20CI%20with%20Gradle/badge.svg)
![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue)

A modern Spring Boot application for managing meal kit subscriptions, built with Domain-Driven Design principles and comprehensive null safety.

## About

Mealkit is a subscription-based meal kit service that enables customers to select recipes, manage deliveries, and track order history. The application provides:

- **Customer Authentication**: Secure signup and login with JWT-based authentication
- **Recipe Catalog**: Browse recipes with detailed ingredients, quantities, and pricing
- **Subscription Management**: Create and manage meal kit subscriptions with flexible delivery scheduling
- **Order Processing**: Track upcoming orders, modify recipes, and view delivery history
- **Backoffice Operations**: Administrative tools for order fulfillment and user management
- **Dynamic Pricing**: Automatic price calculation based on recipe complexity and ingredients

## Technology Stack

### Backend
- **Java 25** with toolchain configuration
- **Spring Boot 3.5.9** (Spring Web, Spring Data JPA, Spring Security)
- **JWT Authentication** with OAuth2 Resource Server

### Database & Persistence
- **PostgreSQL 18.1** with optimized configuration
- **Flyway** for database migrations
- **jOOQ 3.20.10** for type-safe SQL queries
- **db-scheduler** for background job processing

### Testing
- **Spock Framework 2.4** for BDD-style testing
- **Zonky Embedded PostgreSQL** with Docker provider
- **Testcontainers** for integration testing
- **Awaitility** for asynchronous testing

### Code Quality
- **Error Prone 2.44.0** for static analysis
- **NullAway 0.12.12** for null safety checking
- **JSpecify 1.0.0** for standardized null annotations
- **Lombok** for reducing boilerplate

### Other Tools
- **Caffeine** for caching
- **UUID Creator** for UUID generation
- **DataFaker** for test data generation

## Prerequisites

- **Java 25 JDK** (configured via Gradle toolchain)
- **Docker** (required for jOOQ code generation and tests)
- **Gradle** (wrapper included - no installation needed)

## Getting Started

```bash
# Generate jOOQ classes from database schema
./gradlew generateJooqClasses

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080` by default.

## Build Commands

### Building
```bash
./gradlew build          # Full build with tests
./gradlew assemble       # Build without running tests
./gradlew clean build    # Clean build from scratch
```

### Testing
```bash
./gradlew test                                    # Run all tests
./gradlew test --tests ClassName                  # Run specific test class
./gradlew test --tests ClassName.methodName       # Run specific test method
```

### Running
```bash
./gradlew bootRun        # Run the Spring Boot application
```

### Other Utilities
```bash
./gradlew bootJar        # Create executable JAR
./gradlew bootBuildImage # Build OCI/Docker image
./gradlew dependencies   # View dependency tree
./gradlew javadoc        # Generate API documentation
```

### jOOQ Code Generation

Generate type-safe database access classes from your schema:

```bash
./gradlew generateJooqClasses
```

**Note:** Requires Docker to be running. The plugin:
- Starts a PostgreSQL 18.1 container (reuses existing if available)
- Runs Flyway migrations to create the schema
- Generates jOOQ classes from the actual database schema

## Architecture

Mealkit follows **Domain-Driven Design (DDD)** principles with clear separation of concerns:

### Core Patterns

- **Single-Use-Case Application Services**: Each use case has a dedicated service (e.g., `CreateRecipeService`, `UpdateUpcomingOrderRecipesService`)
- **Domain Validation**: Domain models enforce invariants through factory methods and validation
- **Persistence/Domain Separation**: Domain models separated from persistence concerns
- **Type-Safe IDs**: Generic `Id<T>` wrapper prevents mixing entity identifiers (e.g., `Id<Recipe>` vs `Id<Customer>`)

### Validation & Error Handling

- `ValidationException` for business rule violations (mapped to HTTP 422)
- `GlobalExceptionHandler` with `@ControllerAdvice` for centralized error handling
- Domain models make invalid states impossible to represent

### Database Layer

- **Flyway migrations** in `src/main/resources/db/migration/`
- Sequential versioning: `V1__description.sql`, `V2__description.sql`, etc.
- Hibernate configured with `ddl-auto: validate` - schema changes require migrations
- Never modify existing migrations after commit

### Testing Architecture

- **Zonky Embedded PostgreSQL**: Fast test execution with Docker-based containers reused across test runs
- **Spock Framework**: BDD-style tests with `given-when-then` structure
- **ApplicationRunner Pattern**: High-level API interaction methods for tests
- **ApiResponse Pattern**: Type-safe response handling with `Success<T>` and `Error<T>` sealed interfaces
- **Performance Optimizations**: tmpfs storage, parallel execution, tuned PostgreSQL settings

## API Endpoints

### Authentication
```
POST   /customers/signup    # Create new customer account
POST   /customers/login     # Authenticate and receive JWT token
```

### Recipes
```
GET    /recipes             # List all recipes
GET    /recipes/{id}        # Get recipe details
POST   /recipes             # Create new recipe
```

### Subscriptions
```
POST   /subscriptions                              # Create subscription with initial order
GET    /subscriptions                              # Get customer's active subscription
GET    /subscriptions/history                      # Get delivered order history
GET    /subscriptions/upcoming-orders/{orderId}    # Get upcoming order details
PUT    /subscriptions/upcoming-orders/{orderId}/recipes        # Update recipes for upcoming order
PUT    /subscriptions/delivery-day                             # Update default delivery day
PUT    /subscriptions/upcoming-orders/{orderId}/delivery-day   # Update delivery day for specific order
```

### Orders (Backoffice)
```
POST   /orders/{orderId}/delivered    # Mark order as delivered (requires BACKOFFICE role)
```

All authenticated endpoints require `Authorization: Bearer <jwt-token>` header.

## Testing

### Spock BDD Tests

Tests follow behavior-driven development style with clear `given-when-then` blocks:

```groovy
def "customer can create subscription with 3 recipes"() {
    given: "customer is authenticated"
        def token = app.signupCustomer(email, password).token()
    
    and: "3 recipes exist in catalog"
        def recipes = [recipe1, recipe2, recipe3]
    
    when: "customer creates subscription with these recipes"
        def subscription = app.createSubscription(token, recipes)
    
    then: "subscription is created with upcoming order"
        subscription.upcomingOrders.size() == 1
        subscription.upcomingOrders[0].recipeIds == recipes
}
```

### Test Performance

PostgreSQL test instances are optimized for speed:
- **tmpfs storage**: Database files stored in memory
- **Parallel execution**: Tests run concurrently (defaults to CPU cores / 2)
- **Container reuse**: PostgreSQL containers reused across test runs
- **Tuned settings**: Disabled fsync, increased buffers, optimized checkpoints

### Test-Driven Development

The project follows TDD workflow:
1. Receive Spock test specification with descriptive text
2. Implement test with setup, actions, and assertions
3. Verify test fails for the right reason
4. Implement production code to pass the test
5. Verify all tests pass

## Code Quality

### Null Safety with NullAway

All code under `com.urgoringo` is null-safe by default through NullAway static analysis:

```java
public class RecipeService {
    // All parameters and returns are non-null by default
    public Recipe getRecipe(Id<Recipe> id) { ... }
    
    // Use @Nullable for values that can be null
    public @Nullable Recipe findRecipe(Id<Recipe> id) { ... }
    
    // Parameters can also be nullable
    public void updateDescription(@Nullable String description) { ... }
}
```

**Key Features:**
- **Compile-time checking**: Build fails if null safety violations detected
- **JSpecify annotations**: Industry-standard null annotations (v1.0.0)
- **Error Prone integration**: Catches common mistakes during compilation
- **Generated code excluded**: Lombok and jOOQ output automatically excluded

### Static Analysis

- **Error Prone 2.44.0**: Catches common Java mistakes at compile-time
- **NullAway 0.12.12**: Fast null-safety analysis with minimal overhead
- **JSpecify Mode**: Full nullness checking on arrays, generics, and type parameters

## CI/CD

The project uses **GitHub Actions** for continuous integration:

- **Workflow**: Java CI with Gradle
- **Triggers**: Push to `master` branch and pull requests
- **Jobs**:
  - Build and test with JDK 25
  - Dependency graph generation for Dependabot
  - Automated dependency alerts

Configuration: `.github/workflows/gradle.yml`

## Development Guidelines

### Code Style

- **Clear naming over comments**: Code should be self-explanatory
- **Minimal comments**: Only when explaining *why*, not *what*
- **Test-driven development**: Write tests before implementation
- **Domain-focused**: Business logic in domain models, not services

### Commit Messages

Keep commits concise and focused on business value:

```
Refactor ingredients to use entity-value object pattern

Enables ingredient reuse across recipes and prevents inconsistent names.
```

**Avoid:** Repeating implementation details visible in the diff

### Adding Database Changes

1. Create Flyway migration: `V{next-number}__{description}.sql`
2. Use sequential versioning (V1, V2, V3, etc.)
3. Run tests to validate migration
4. Never modify committed migrations

### Detailed Guidelines

For comprehensive development guidelines, architecture details, and best practices, see [AGENTS.md](./AGENTS.md).

## License

This project is licensed under the terms specified in the LICENSE file.
