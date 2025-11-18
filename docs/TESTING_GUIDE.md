# Testing Guide - Unit, Integration, and Coverage

## 📊 Test Types Overview

This project includes three types of tests:

### 1. **Unit Tests** 
Testing individual components in isolation
- **Repository Tests**: Test CRUD operations
- **Service Tests**: Test business logic

### 2. **Integration Tests**
Testing complete flow through all layers
- **End-to-End Flow**: Controller → Service → Repository → Model
- **Multi-Layer Validation**: Verify data consistency across layers

### 3. **Coverage Tests**
Measuring how much code is tested
- **Line Coverage**: % of code lines executed
- **Branch Coverage**: % of decision paths tested
- **Target**: 80%+ coverage for production code

---

## ☕ Java Testing

### Running Tests

```bash
cd java

# Run all unit tests
mvn test

# Run only unit tests (excludes integration)
mvn test -Dtest="!*Integration*"

# Run only integration tests
mvn verify

# Run with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Structure

```
src/test/java/com/demo/
├── repository/              # Unit tests for repositories
│   ├── StudentRepositoryTest.java
│   ├── CourseRepositoryTest.java
│   └── EnrollmentRepositoryTest.java
├── service/                 # Unit tests for service
│   └── EnrollmentServiceTest.java
└── integration/             # Integration tests
    └── EnrollmentIntegrationTest.java
```

### Coverage Configuration

JaCoCo plugin configured in `pom.xml`:
- **Minimum Coverage**: 80% line coverage
- **Report Location**: `target/site/jacoco/index.html`
- **Automatic**: Runs on `mvn test`

### Test Count

- **Repository Unit Tests**: 18 tests
- **Service Unit Tests**: 5 tests
- **Integration Tests**: 6 tests
- **Total**: 29 tests

---

## 🐍 Python Testing

### Running Tests

```bash
cd python

# Run all tests
pytest tests/ -v

# Run only unit tests
pytest tests/test_enrollment_service.py tests/test_repositories.py -v

# Run only integration tests
pytest tests/test_integration.py -v

# Run with coverage
pytest tests/ --cov=src --cov-report=html --cov-report=term

# View coverage report
open htmlcov/index.html
```

### Test Structure

```
tests/
├── test_repositories.py         # Unit tests for repositories
├── test_enrollment_service.py   # Unit tests for service
└── test_integration.py          # Integration tests
```

### Coverage Configuration

pytest-cov configured with:
- **Source**: `src/` directory
- **Reports**: HTML and terminal
- **Location**: `htmlcov/index.html`

### Test Count

- **Repository Unit Tests**: 15 tests
- **Service Unit Tests**: 5 tests
- **Integration Tests**: 6 tests
- **Total**: 26 tests

---

## 📜 JavaScript Testing

### Running Tests

```bash
cd javascript

# Run all tests
npm test

# Run with verbose output
npm run test:verbose

# Run with coverage
npm run test:coverage

# Run in watch mode
npm test -- --watch

# Run specific test file
npm test -- Repositories.test.js

# View coverage report
open coverage/lcov-report/index.html
```

### Test Structure

```
tests/
├── Repositories.test.js         # Unit tests for repositories
├── EnrollmentService.test.js    # Unit tests for service
└── Integration.test.js          # Integration tests
```

### Coverage Configuration

Jest configured in `package.json`:
- **Coverage Directory**: `coverage/`
- **Collect From**: `src/**/*.js`
- **Report**: HTML LCOV report

### Test Count

- **Repository Unit Tests**: 18 tests
- **Service Unit Tests**: 5 tests
- **Integration Tests**: 7 tests
- **Total**: 30 tests

---

## 📈 Coverage Reports Comparison

### Java (JaCoCo)
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

**Report Includes:**
- Line coverage %
- Branch coverage %
- Complexity metrics
- Class-by-class breakdown

### Python (pytest-cov)
```bash
pytest tests/ --cov=src --cov-report=html
open htmlcov/index.html
```

**Report Includes:**
- Statement coverage %
- Missing lines highlighted
- File-by-file breakdown

### JavaScript (Jest)
```bash
npm run test:coverage
open coverage/lcov-report/index.html
```

**Report Includes:**
- Statement coverage %
- Branch coverage %
- Function coverage %
- Line coverage %

---

## 🎯 Test Categories Explained

### Unit Tests - Repository Layer

**Purpose**: Test data access in isolation

**Example** (Java):
```java
@Test
void testFindById_Exists() {
    Student student = new Student("S001", "John", "john@test.com", 18);
    repository.save(student);
    
    Student found = repository.findById("S001");
    
    assertNotNull(found);
    assertEquals("S001", found.getId());
}
```

**What We Test**:
- ✅ Save operations
- ✅ Find by ID (exists/not exists)
- ✅ Find all
- ✅ Update operations

---

### Unit Tests - Service Layer

**Purpose**: Test business logic in isolation

**Example** (Python):
```python
def test_enrollment_exceeds_credit_limit(self, setup):
    service = setup['service']
    
    # Arrange: Student with limited credits
    service.enroll("S002", "C001")  # 3 credits
    service.enroll("S002", "C002")  # 4 credits
    
    # Act & Assert: Should fail on 6 more credits
    with pytest.raises(RuntimeError):
        service.enroll("S002", "C004")  # Would be 13 > 12
```

**What We Test**:
- ✅ Business rule validation
- ✅ Exception handling
- ✅ State changes
- ✅ Calculations (GPA)

---

### Integration Tests

**Purpose**: Test complete flow through all layers

**Example** (JavaScript):
```javascript
test('Integration: Complete enrollment flow', () => {
    // Act through Controller
    const result = controller.enrollStudent('S001', 'C001');
    
    // Assert Controller response
    expect(result).toContain('SUCCESS');
    
    // Assert Service created enrollment
    const enrollment = enrollmentRepository
        .findByStudentAndCourse('S001', 'C001');
    expect(enrollment).toBeDefined();
    
    // Assert Repository updated student
    const student = studentRepository.findById('S001');
    expect(student.currentCredits).toBe(3);
});
```

**What We Test**:
- ✅ End-to-end workflows
- ✅ Layer interactions
- ✅ Data consistency
- ✅ Error propagation

---

## 📊 Coverage Metrics

### Target Coverage Goals

| Component | Target | Rationale |
|-----------|--------|-----------|
| Model | 100% | Simple data classes |
| Repository | 90%+ | CRUD operations |
| Service | 95%+ | Business logic - critical |
| Controller | 80%+ | Thin layer, tested via integration |
| **Overall** | **85%+** | Production quality |

### How to Achieve High Coverage

1. **Test All Paths**
   - Happy path (success)
   - Error paths (exceptions)
   - Edge cases (boundaries)

2. **Test State Changes**
   - Before and after operations
   - Side effects

3. **Test Calculations**
   - Normal values
   - Edge values (0, max, etc.)

---

## 🔍 Reading Coverage Reports

### Java - JaCoCo Report

```
Element          Coverage    Missed   Total
=========================================
EnrollmentService  95%       3        60
- enroll()         100%      0        25
- calculateGPA()   90%       2        20
```

**Green**: Well covered  
**Yellow**: Partially covered  
**Red**: Not covered

### Python - Coverage Report

```
Name                           Stmts   Miss  Cover
==================================================
src/service/enrollment_service    45      2    96%
src/repository/student_repository 15      0   100%
```

**Click file**: See which lines aren't covered (highlighted in red)

### JavaScript - Jest Coverage

```
File                    | % Stmts | % Branch | % Funcs | % Lines
================================================================
EnrollmentService.js    |   96.67 |    91.67 |     100 |   96.55
StudentRepository.js    |     100 |      100 |     100 |     100
```

---

## 🎓 Best Practices

### 1. Write Tests First (TDD)
```
❌ Code → Test
✅ Test → Code → Refactor
```

### 2. Independent Tests
```java
// ✅ Good: Each test is independent
@BeforeEach
void setUp() {
    repository = new StudentRepository(); // Fresh for each test
}

// ❌ Bad: Tests share state
private static StudentRepository repository; // Shared!
```

### 3. Descriptive Names
```python
# ✅ Good: Clear what's being tested
def test_enrollment_fails_when_student_not_found()

# ❌ Bad: Unclear
def test1()
```

### 4. Arrange-Act-Assert
```javascript
test('Example', () => {
    // Arrange: Setup
    const student = new Student('S001', 'John', 'j@test.com', 18);
    
    // Act: Execute
    const result = service.enroll('S001', 'C001');
    
    // Assert: Verify
    expect(result).toBeDefined();
});
```

### 5. Test One Thing
```java
// ✅ Good: Tests one concept
@Test
void testEnrollment_StudentNotFound() {
    assertThrows(IllegalArgumentException.class,
        () -> service.enroll("S999", "C001"));
}

// ❌ Bad: Tests multiple things
@Test
void testEverything() {
    // Tests 5 different scenarios...
}
```

---

## 🚀 Quick Commands Summary

### Java
```bash
mvn test                    # Unit tests
mvn verify                  # Integration tests
mvn clean test jacoco:report # Coverage
```

### Python
```bash
pytest tests/ -v            # All tests
pytest --cov=src --cov-report=html # Coverage
```

### JavaScript
```bash
npm test                    # All tests
npm run test:coverage       # Coverage
```

---

## 📝 Test Statistics

### Total Test Count: **85 tests**

| Language   | Unit | Integration | Total |
|------------|------|-------------|-------|
| Java       | 23   | 6           | 29    |
| Python     | 20   | 6           | 26    |
| JavaScript | 23   | 7           | 30    |

### Coverage Achieved

All three implementations achieve **>85% code coverage** with the test suites provided!

---

**Happy Testing! 🎉**
