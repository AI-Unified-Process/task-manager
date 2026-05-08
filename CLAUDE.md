# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

This is a **case study application** for the book *Spec-driven Development with the AI Unified Process (AIUP)*. The
primary goal is to demonstrate how specifications, code, and tests stay synchronized using a spec-driven approach with
AI assistance.

**Important**: This is NOT a production-ready task manager. It's a didactic example focused on correctness over
completeness, designed to teach spec-driven development principles.

## Core Principle: Specifications as Source of Truth

The `/docs` directory is the **single source of truth** for system behavior. All code and tests MUST reflect what is
defined there.

```
/docs
├── vision.md              # Business goals and scope
├── requirements.md        # FR, NFR, and constraints with stable IDs
├── entity_model.md        # Database schema with ER diagram
├── use_cases.puml         # Use case diagram
└── /use_cases             # Use case specifications
```

**Critical Rule**: When making changes, verify against specifications first. Never add features or business rules not
defined in `/docs`.

## Technology Stack

- **Framework**: Vaadin 25.1.5 (Java-based UI framework)
- **Backend**: Spring Boot 4.0.2, Java 25
- **Database**: PostgreSQL with jOOQ for type-safe SQL
- **Migrations**: Flyway (located in `src/main/resources/db/migration`)
- **Security**: Spring Security with JWT OAuth2
- **Testing**:
    - Vaadin Browserless Testing for server-side Vaadin unit tests
    - Playwright with Mopo for integration tests
    - Testcontainers for database testing
    - ArchUnit for architecture validation

## Build and Development Commands

### Running the Application

```bash
# Start application in development mode (default goal)
mvn spring-boot:test-run

# Build the application
mvn compile

# Run with production build
mvn clean package
mvn spring-boot:run
```

### Database Setup

The database is configured in `src/main/resources/application.properties`:

- URL: `jdbc:postgresql://localhost:5432/aiup-task-manager`
- Username: `aiup-task-manager`
- Password: `aiup-task-manager`

Flyway migrations run automatically during the build in the `generate-sources` phase using Testcontainers.

### Code Generation

jOOQ code generation happens automatically during `mvn clean package`:

1. Groovy plugin starts a Testcontainer with PostgreSQL
2. Flyway runs migrations from `src/main/resources/db/migration`
3. jOOQ generates type-safe Java classes to `ch.martinelli.demo.aiup.db` package

To regenerate jOOQ code after schema changes:

```bash
mvn clean generate-sources
```

### Testing

```bash
# Run all tests (unit + integration)
mvn verify

# Run only unit tests (excluding *IT.java)
mvn test

# Run only integration tests
mvn failsafe:integration-test

# Run tests with coverage
mvn verify -Pcoverage
# Coverage report: target/site/jacoco/index.html
```

**Single Test Execution**:

```bash
# Run a single unit test class
mvn test -Dtest=LoginViewTest

# Run a single integration test class
mvn failsafe:integration-test -Dit.test=HelloWorldViewIT
```

### Code Quality

```bash
# Format check (Spring Java Format)
mvn spring-javaformat:validate

# Apply formatting
mvn spring-javaformat:apply
```

The project uses:

- ErrorProne for static analysis
- NullAway for nullability checking
- Spring Java Format (enforced on `validate` phase)

## Architecture

### Layered Architecture

The codebase follows a strict layered architecture enforced by ArchUnit tests:

- **UI Layer** (`..ui..`): Vaadin views and components
    - May not be accessed by any other layer
    - Can access Domain and Security layers
- **Security Layer** (`..security..`): Authentication and authorization
    - Can access Domain layer
- **Domain Layer** (`..domain..`): Business logic and data access
    - May only be accessed by UI and Security layers

### Module Structure

The application is organized into feature modules with a `core` module for shared infrastructure:

- **core**: Shared UI components, configuration, security, i18n
    - `core.ui`: MainLayout, shared components, base test classes
    - `core.security`: Security configuration
    - `core.configuration`: jOOQ and application configuration
- **Feature Modules** (e.g., `task`, 'team'): Isolated business features
    - Each module contains: `domain` and `ui` subpackages
    - Modules must NOT depend on each other (only on `core`)
    - Core must NOT depend on feature modules

**Architecture Rules** (see `ArchitectureTest.java`):

- Only UI and Security layers may use Vaadin classes
- Feature modules are independent (no cross-module dependencies)
- Core module is shared infrastructure only

### Package Naming

Base package: `ch.martinelli.demo.aiup`

- Generated jOOQ classes: `ch.martinelli.demo.aiup.db`

## Testing Strategy

### Test Types

1. **Browserless Tests** (`*Test.java`): Fast, server-side Vaadin component tests
    - Extend `AbstractBrowserlessTest` base class (which extends `SpringBrowserlessTest`)
    - Browserless framework auto-bootstraps the UI and discovers routes
    - Use Spring Security annotations (`@WithMockUser`, `@WithAnonymousUser`) for authentication
    - Best for testing business logic and component behavior

2. **Playwright Integration Tests** (`*IT.java`): Full browser-based tests
    - Extend `PlaywrightIT` base class
    - Use Mopo helper for Vaadin component interaction
    - Run with headless Chrome by default
    - Best for testing complete user workflows

3. **Architecture Tests** (`ArchitectureTest.java`): Enforce structural rules
    - Validates layer dependencies
    - Ensures module isolation
    - Checks Vaadin usage boundaries

### Test Infrastructure

Both test types use **Testcontainers** for PostgreSQL, configured via `TestcontainersConfiguration.class`.

**Browserless Test Template**:

```java

@WithMockUser(username = "testuser", roles = "USER")
class MyViewTest extends AbstractBrowserlessTest {
    @Test
    void test_something() {
        var view = navigate(MyView.class);
        // Find components: $(Component.class).withCaption("Label").single()
        // Interact: test(button).click(), test(field).setValue("x")
        // Grids: test(grid).size(), test(grid).getRow(i), test(grid).getCellComponent(i, "col")
    }
}
```

**Playwright Test Template**:

```java

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class MyViewIT extends PlaywrightIT {
    @Test
    void test_something() {
        page.navigate("http://localhost:" + localServerPort);
        // Use mopo or page for interactions
    }
}
```

## Database Conventions

- **Sequences**: All auto-increment primary keys use PostgreSQL sequences
    - Naming: `{table_name}_seq` (e.g., `task_seq`, `team_seq`)
- **Migrations**: Named `V{version}__{description}.sql`
    - Example: `V1__create_sequences.sql`
- **Table Names**: Lowercase with underscores (snake_case)
- **jOOQ**: Enable optimistic locking via `VjJooqConfiguration`

## Development Workflow

When implementing new features or changes:

1. **Start with specifications**: Check `/docs` for requirements and use cases
2. **Verify database schema**: Update `docs/entity_model.md` if needed
3. **Create/update Flyway migration**: Add to `src/main/resources/db/migration`
4. **Regenerate jOOQ**: Run `mvn clean generate-sources`
5. **Implement domain logic**: Add to appropriate module's `domain` package
6. **Create UI**: Add Vaadin views to module's `ui` package
7. **Write tests**: Both Karibu (fast) and Playwright (comprehensive) tests
8. **Verify architecture**: Run `ArchitectureTest` to ensure compliance
9. **Update specifications**: Keep `/docs` synchronized with implementation

## Important Notes

- **No Over-Engineering**: Only implement what's in the specifications. Don't add "nice to have" features.
- **Naming Stability**: Use stable IDs (FR-001, UC-001, etc.) to maintain traceability
- **Test Coverage**: JaCoCo coverage excludes Application class and generated jOOQ code
- **JWT Secret**: Change `jwt.auth.secret` in production (generate with `openssl rand -base64 32`)
- **Debug Port**: Application runs with remote debugging on port 5679
