# Java Testing Demo - Layer by Layer

## Project Structure
```
java/
├── src/
│   ├── main/java/com/demo/
│   │   ├── model/           # Model Layer (Student, Course, Enrollment)
│   │   ├── repository/      # Repository Layer (In-memory data storage)
│   │   ├── service/         # Service Layer (Business logic - FOCUS OF TESTING)
│   │   └── controller/      # Controller Layer (Request handling)
│   └── test/java/com/demo/
│       ├── repository/      # Unit tests for Repository layer
│       ├── service/         # Unit tests for Service layer
│       └── integration/     # Integration tests (full stack)
└── pom.xml                  # Maven configuration with JaCoCo
```

## How to Run

### Run All Tests
```bash
cd java
mvn test
```

### Run Only Unit Tests
```bash
mvn test -Dtest="!*Integration*"
```

### Run Only Integration Tests
```bash
mvn verify
```

### Run with Coverage Report
```bash
mvn clean test jacoco:report
# View report: open target/site/jacoco/index.html
```

### Compile Project
```bash
mvn compile
```

## Test Suites

### Unit Tests (23 tests)

**Repository Tests** (18 tests):
- `StudentRepositoryTest` - CRUD operations for students
- `CourseRepositoryTest` - CRUD operations for courses
- `EnrollmentRepositoryTest` - CRUD operations for enrollments

**Service Tests** (5 tests):
- `EnrollmentServiceTest` - Business logic validation
  1. Student Not Found
  2. Course Not Found
  3. Credit Limit Exceeded
  4. Successful Enrollment
  5. GPA Calculation

### Integration Tests (6 tests)

**EnrollmentIntegrationTest** - Full stack testing:
1. Complete enrollment flow (Controller → Service → Repository)
2. Invalid student handling
3. Multiple enrollments and GPA calculation
4. Credit limit enforcement across layers
5. Student lifecycle (end-to-end)
6. Concurrent enrollments

## Coverage

- **Tool**: JaCoCo
- **Target**: 80% line coverage
- **Report**: `target/site/jacoco/index.html`
- **Current**: 85%+ coverage achieved

## Key Concepts

- **Unit Testing**: Testing individual components in isolation
- **Integration Testing**: Testing complete flow through all layers
- **Code Coverage**: Measuring % of code tested
- **Test Setup**: Using `@BeforeEach` to initialize test data
- **Assertions**: Using JUnit assertions to verify expected behavior
- **Exception Testing**: Using `assertThrows()` to test error conditions
