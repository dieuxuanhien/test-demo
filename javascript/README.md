# JavaScript Testing Demo - Layer by Layer

## Project Structure
```
javascript/
├── src/
│   ├── model/           # Model Layer (Student, Course, Enrollment)
│   ├── repository/      # Repository Layer (In-memory data storage)
│   ├── service/         # Service Layer (Business logic - FOCUS OF TESTING)
│   └── controller/      # Controller Layer (Request handling)
├── tests/
│   ├── Repositories.test.js        # Unit tests for Repository layer
│   ├── EnrollmentService.test.js   # Unit tests for Service layer
│   └── Integration.test.js         # Integration tests (full stack)
└── package.json         # npm configuration with Jest
```

## How to Run

### Install Dependencies
```bash
cd javascript
npm install
```

### Run All Tests
```bash
npm test
```

### Run Tests with Verbose Output
```bash
npm run test:verbose
```

### Run with Coverage Report
```bash
npm run test:coverage
# View report: open coverage/lcov-report/index.html
```

### Run Specific Test File
```bash
npm test -- Repositories.test.js
```

### Watch Mode (Re-run on Changes)
```bash
npm test -- --watch
```

## Test Suites

### Unit Tests (23 tests)

**Repository Tests** (18 tests):
- `StudentRepository` - CRUD operations for students
- `CourseRepository` - CRUD operations for courses
- `EnrollmentRepository` - CRUD operations for enrollments

**Service Tests** (5 tests):
- `EnrollmentService` - Business logic validation
  1. Student Not Found
  2. Course Not Found
  3. Credit Limit Exceeded
  4. Successful Enrollment
  5. GPA Calculation

### Integration Tests (7 tests)

**Enrollment Integration Tests** - Full stack testing:
1. Complete enrollment flow (Controller → Service → Repository)
2. Invalid student handling
3. Multiple enrollments and GPA calculation
4. Credit limit enforcement across layers
5. Student lifecycle (end-to-end)
6. Concurrent enrollments
7. Error handling propagation

## Coverage

- **Tool**: Jest
- **Target**: 80%+ coverage
- **Report**: `coverage/lcov-report/index.html`
- **Current**: 85%+ coverage achieved

## Key Concepts

- **Unit Testing**: Testing individual components in isolation
- **Integration Testing**: Testing complete flow through all layers
- **Code Coverage**: Measuring % of code tested
- **Test Suites**: Using `describe()` to group related tests
- **Test Setup**: Using `beforeEach()` to initialize test data
- **Assertions**: Using Jest matchers (`expect`, `toBe`, `toThrow`, etc.)
- **Exception Testing**: Using `expect().toThrow()` to test error conditions
