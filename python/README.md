# Python Testing Demo - Layer by Layer

## Project Structure
```
python/
├── src/
│   ├── model/           # Model Layer (Student, Course, Enrollment)
│   ├── repository/      # Repository Layer (In-memory data storage)
│   ├── service/         # Service Layer (Business logic - FOCUS OF TESTING)
│   └── controller/      # Controller Layer (Request handling)
├── tests/
│   ├── test_repositories.py        # Unit tests for Repository layer
│   ├── test_enrollment_service.py  # Unit tests for Service layer
│   └── test_integration.py         # Integration tests (full stack)
└── requirements.txt     # Python dependencies (pytest, pytest-cov)
```

## How to Run

### Install Dependencies
```bash
cd python
pip install -r requirements.txt
```

### Run All Tests
```bash
pytest tests/ -v
```

### Run Only Unit Tests
```bash
pytest tests/test_enrollment_service.py tests/test_repositories.py -v
```

### Run Only Integration Tests
```bash
pytest tests/test_integration.py -v
```

### Run with Coverage Report
```bash
pytest tests/ --cov=src --cov-report=html --cov-report=term
# View report: open htmlcov/index.html
```

## Test Suites

### Unit Tests (20 tests)

**Repository Tests** (15 tests):
- `TestStudentRepository` - CRUD operations for students
- `TestCourseRepository` - CRUD operations for courses
- `TestEnrollmentRepository` - CRUD operations for enrollments

**Service Tests** (5 tests):
- `TestEnrollmentService` - Business logic validation
  1. Student Not Found
  2. Course Not Found
  3. Credit Limit Exceeded
  4. Successful Enrollment
  5. GPA Calculation

### Integration Tests (6 tests)

**TestEnrollmentIntegration** - Full stack testing:
1. Complete enrollment flow (Controller → Service → Repository)
2. Invalid student handling
3. Multiple enrollments and GPA calculation
4. Credit limit enforcement across layers
5. Student lifecycle (end-to-end)
6. Concurrent enrollments

## Coverage

- **Tool**: pytest-cov
- **Target**: 80%+ coverage
- **Report**: `htmlcov/index.html`
- **Current**: 90%+ coverage achieved

## Key Concepts

- **Unit Testing**: Testing individual components in isolation
- **Integration Testing**: Testing complete flow through all layers
- **Code Coverage**: Measuring % of code tested
- **Fixtures**: Using `@pytest.fixture` to set up test data
- **Exception Testing**: Using `pytest.raises()` to test error conditions
- **Assertions**: Using `assert` statements to verify expected behavior
- **Test Discovery**: Pytest automatically discovers tests starting with `test_`
