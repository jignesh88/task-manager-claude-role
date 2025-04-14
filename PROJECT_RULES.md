# PROJECT_RULES.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands
- Build: `./mvnw clean install`
- Run: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run single test: `./mvnw test -Dtest=TestClassName#testMethodName`
- Verify and check: `./mvnw clean verify`

## Code Style Guidelines
- Use Java records for domain models with @NonNull annotations
- Follow camelCase for methods/variables, PascalCase for classes
- Suffix conventions: Controller, Service, Repository, Exception, Dto
- Group imports: java core, spring, application-specific (alphabetical)
- Factory methods for object creation (e.g., Task.createNew)
- Use Lombok annotations (@RequiredArgsConstructor for DI)
- Global exception handling with @RestControllerAdvice
- Test naming: descriptive_whenCondition_thenExpectedResult
- Use Spring MockMvc for controller tests with @WebMvcTest
- Use records for immutable data, create defensive copies