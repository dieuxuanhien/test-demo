# Testing Demonstration Repository

This repository demonstrates comprehensive testing strategies across three programming languages: **Java**, **JavaScript**, and **Python**. Each implementation showcases the same enrollment management system with both **unit tests** and **integration tests**.

## 📋 Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Testing Concepts](#testing-concepts)
- [Java Testing](#java-testing)
- [JavaScript Testing](#javascript-testing)
- [Python Testing](#python-testing)
- [Running Tests](#running-tests)
- [Test Coverage](#test-coverage)

## 🎯 Overview

This project implements a simple student enrollment system with three architectural layers:
- **Model Layer**: Student, Course, Enrollment entities
- **Repository Layer**: Data access (in-memory storage)
- **Service Layer**: Business logic
- **Controller Layer**: Request handling

The testing strategy demonstrates:
1. **Unit Tests** with **Mocks** and **Stubs** (test doubles)
2. **Integration Tests** with real object collaboration

## 📁 Project Structure

```
demo-unit-integration-test/
├── java/                           # Java implementation
│   ├── src/
│   │   ├── main/java/com/demo/
│   │   │   ├── model/             # Domain models
│   │   │   ├── repository/        # Data access
│   │   │   ├── service/           # Business logic
│   │   │   └── controller/        # Request handlers
│   │   └── test/java/com/demo/
│   │       ├── service/           # Unit tests
│   │       └── integration/       # Integration tests
│   └── pom.xml                    # Maven configuration
│
├── javascript/                     # JavaScript implementation
│   ├── src/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── controller/
│   │   └── __tests__/             # All tests
│   └── package.json               # npm configuration
│
└── python/                         # Python implementation
    ├── src/
    │   ├── model/
    │   ├── repository/
    │   ├── service/
    │   └── controller/
    ├── tests/                      # All tests
    └── requirements.txt            # pip dependencies
```

## 🧪 Testing Concepts

### Unit Tests

**Purpose**: Test individual components in isolation

**Characteristics**:
- ✅ Fast execution
- ✅ Isolated from external dependencies
- ✅ Uses test doubles (mocks/stubs)
- ✅ Tests one component at a time

**Test Doubles Used**:

1. **MOCK**: Simulated objects that verify interactions
   - Example: Verify that `save()` was called with correct parameters
   - In Java: `@Mock` annotation (Mockito)
   - In JavaScript: `jest.fn()`
   - In Python: `Mock()` from `unittest.mock`

2. **STUB**: Simulated objects that return predefined responses
   - Example: `findById()` returns a specific student object
   - In Java: `when().thenReturn()` (Mockito)
   - In JavaScript: `mockReturnValue()`
   - In Python: `return_value` attribute

### Integration Tests

**Purpose**: Test multiple components working together

**Characteristics**:
- ✅ Tests real object collaboration
- ✅ No mocks/stubs (uses real implementations)
- ✅ Validates layer integration
- ✅ Tests complete workflows

**What's Tested**:
- Controller → Service → Repository flow
- Data persistence across layers
- Business rule enforcement
- Error propagation
- State management

## ☕ Java Testing

### Framework & Tools
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Maven Surefire**: Unit test runner
- **Maven Failsafe**: Integration test runner
- **JaCoCo**: Code coverage

### Unit Test Example (EnrollmentServiceTest.java)

```java
@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {
    @Mock
    private StudentRepository studentRepository;  // MOCK
    
    @Mock
    private CourseRepository courseRepository;    // MOCK
    
    @Test
    void testEnroll_Success() {
        // STUB - Define return values
        when(studentRepository.findById("S001")).thenReturn(student);
        when(courseRepository.findById("C001")).thenReturn(course);
        
        // ACT
        Enrollment result = enrollmentService.enroll("S001", "C001");
        
        // VERIFY mock interactions
        verify(studentRepository).findById("S001");
        verify(enrollmentRepository).save(any(Enrollment.class));
    }
}
```

### Integration Test Example (EnrollmentIntegrationTest.java)

```java
public class EnrollmentIntegrationTest {
    // REAL instances - NO MOCKS!
    private StudentRepository studentRepository;
    private EnrollmentService enrollmentService;
    private EnrollmentController enrollmentController;
    
    @BeforeEach
    void setUp() {
        // Wire up real dependencies
        studentRepository = new StudentRepository();
        enrollmentService = new EnrollmentService(studentRepository, ...);
        enrollmentController = new EnrollmentController(enrollmentService);
    }
    
    @Test
    void testCompleteEnrollmentFlow() {
        // Test complete flow through all layers
        String result = enrollmentController.enrollStudent("S001", "C001");
        
        // Verify data persisted in repository
        Enrollment saved = enrollmentRepository.findByStudentAndCourse("S001", "C001");
        assertNotNull(saved);
    }
}
```

### Running Java Tests

```bash
cd java

# Run all tests (unit + integration)
mvn test

# Run only unit tests
mvn test -Dtest=*Test

# Run only integration tests
mvn test -Dtest=*IntegrationTest

# Generate coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage Goals
- Line Coverage: **80%+**
- Branch Coverage: **75%+**

## 🟨 JavaScript Testing

### Framework & Tools
- **Jest**: Testing framework (includes mocking)
- **Coverage**: Built-in with Jest

### Unit Test Example (EnrollmentService.test.js)

```javascript
describe('EnrollmentService - Unit Tests', () => {
    let mockStudentRepo;
    let mockCourseRepo;
    
    beforeEach(() => {
        // Create MOCK repositories
        mockStudentRepo = {
            findById: jest.fn(),      // MOCK
            save: jest.fn()
        };
        
        mockCourseRepo = {
            findById: jest.fn()       // MOCK
        };
    });
    
    test('should enroll student successfully', () => {
        // STUB - Define return values
        mockStudentRepo.findById.mockReturnValue(student);
        mockCourseRepo.findById.mockReturnValue(course);
        
        // ACT
        const result = enrollmentService.enroll('S001', 'C001');
        
        // VERIFY
        expect(mockStudentRepo.findById).toHaveBeenCalledWith('S001');
        expect(mockEnrollmentRepo.save).toHaveBeenCalledTimes(1);
    });
});
```

### Integration Test Example (integration.test.js)

```javascript
describe('Enrollment System - Integration Tests', () => {
    // REAL instances - NO MOCKS!
    let studentRepository;
    let enrollmentService;
    let enrollmentController;
    
    beforeEach(() => {
        // Create real instances
        studentRepository = new StudentRepository();
        enrollmentService = new EnrollmentService(studentRepository, ...);
        enrollmentController = new EnrollmentController(enrollmentService);
        
        setupTestData();
    });
    
    test('should complete enrollment through all layers', () => {
        const result = enrollmentController.enrollStudent('S001', 'C001');
        
        expect(result).toContain('SUCCESS');
        
        // Verify data persisted
        const enrollment = enrollmentRepository.findByStudentAndCourse('S001', 'C001');
        expect(enrollment).toBeDefined();
    });
});
```

### Running JavaScript Tests

```bash
cd javascript

# Run all tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run with coverage
npm run test:coverage

# View coverage report
open coverage/lcov-report/index.html
```

## 🐍 Python Testing

### Framework & Tools
- **pytest**: Testing framework
- **unittest.mock**: Built-in mocking library
- **pytest-cov**: Coverage plugin

### Unit Test Example (test_enrollment_service.py)

```python
class TestEnrollmentService(unittest.TestCase):
    def setUp(self):
        # Create MOCK repositories
        self.mock_student_repo = Mock()      # MOCK
        self.mock_course_repo = Mock()       # MOCK
        
        self.enrollment_service = EnrollmentService(
            self.mock_student_repo,
            self.mock_course_repo,
            self.mock_enrollment_repo
        )
    
    def test_enroll_success(self):
        # STUB - Define return values
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_course_repo.find_by_id.return_value = course
        
        # ACT
        result = self.enrollment_service.enroll('S001', 'C001')
        
        # VERIFY
        self.mock_student_repo.find_by_id.assert_called_once_with('S001')
        self.mock_enrollment_repo.save.assert_called_once()
```

### Integration Test Example (test_integration.py)

```python
class TestEnrollmentIntegration(unittest.TestCase):
    def setUp(self):
        # Create REAL instances - NO MOCKS!
        self.student_repository = StudentRepository()
        self.enrollment_service = EnrollmentService(
            self.student_repository, ...
        )
        self.enrollment_controller = EnrollmentController(
            self.enrollment_service
        )
        self._setup_test_data()
    
    def test_complete_enrollment_flow(self):
        result = self.enrollment_controller.enroll_student('S001', 'C001')
        
        self.assertTrue(result.startswith('SUCCESS'))
        
        # Verify data persisted
        enrollment = self.enrollment_repository.find_by_student_and_course(
            'S001', 'C001'
        )
        self.assertIsNotNone(enrollment)
```

### Running Python Tests

```bash
cd python

# Run all tests
pytest

# Run specific test file
pytest tests/test_enrollment_service.py

# Run with verbose output
pytest -v

# Run with coverage
pytest --cov=src --cov-report=html

# View coverage report
open htmlcov/index.html
```

## 📊 Test Coverage

Each language implementation aims for:

| Metric | Target | Measured By |
|--------|--------|-------------|
| Line Coverage | ≥80% | Lines executed during tests |
| Branch Coverage | ≥75% | Conditional branches tested |
| Function Coverage | ≥90% | Functions/methods called |

### Coverage Reports

**Java**: `java/target/site/jacoco/index.html`
**JavaScript**: `javascript/coverage/lcov-report/index.html`
**Python**: `python/htmlcov/index.html`

## 🎓 Key Learning Points

### 1. **When to Use Unit Tests**
- Testing business logic in isolation
- Fast feedback during development
- Testing edge cases and error conditions
- When external dependencies are slow/unreliable

### 2. **When to Use Integration Tests**
- Verifying components work together
- Testing complete user workflows
- Validating data persistence
- Catching interface mismatches

### 3. **Test Double Decision Guide**

| Need | Use | Example |
|------|-----|---------|
| Verify method was called | **MOCK** | `verify(repo).save(...)` |
| Control return value | **STUB** | `when(repo.find()).thenReturn(student)` |
| Simulate error | **STUB** | `when(repo.find()).thenThrow(Exception)` |

## 🔍 Test Patterns Demonstrated

1. **Arrange-Act-Assert (AAA)**
   - Arrange: Set up test data and stubs
   - Act: Execute the method under test
   - Assert: Verify results and mock interactions

2. **Given-When-Then**
   - Given: Initial context
   - When: Action occurs
   - Then: Expected outcome

3. **Test Isolation**
   - Each test is independent
   - Setup/teardown for clean state
   - No shared mutable state

## 📚 Additional Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [pytest Documentation](https://docs.pytest.org/)
- [Python unittest.mock Guide](https://docs.python.org/3/library/unittest.mock.html)

## 🤝 Contributing

This is a demonstration project. Feel free to:
- Add more test scenarios
- Improve test coverage
- Add other testing patterns
- Implement in other languages

## 📝 License

MIT License - Feel free to use for educational purposes
