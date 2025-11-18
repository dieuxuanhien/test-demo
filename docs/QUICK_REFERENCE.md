# Quick Reference Guide - Testing Layer by Layer

## 🎯 Quick Command Reference

### Java (JUnit 5)
```bash
# Compile
mvn compile

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=EnrollmentServiceTest

# Generate coverage report
mvn test jacoco:report

# Clean and rebuild
mvn clean install
```

### Python (Pytest)
```bash
# Install dependencies
pip install -r requirements.txt

# Run all tests
pytest tests/ -v

# Run specific test
pytest tests/test_enrollment_service.py -v

# Run specific test method
pytest tests/test_enrollment_service.py::TestEnrollmentService::test_enrollment_success -v

# With coverage
pytest tests/ --cov=src --cov-report=html

# Show print statements
pytest tests/ -v -s
```

### JavaScript (Jest)
```bash
# Install dependencies
npm install

# Run all tests
npm test

# Run with verbose output
npm run test:verbose

# Run specific test
npm test -- EnrollmentService.test.js

# With coverage
npm run test:coverage

# Watch mode (re-run on changes)
npm test -- --watch
```

## 📊 Test Assertion Comparison

### Student Not Found Test

**Java (JUnit 5):**
```java
@Test
void testEnrollment_StudentNotFound() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> enrollmentService.enroll("S999", "C001")
    );
    assertTrue(exception.getMessage().contains("Student not found"));
}
```

**Python (Pytest):**
```python
def test_enrollment_student_not_found(self, setup):
    service = setup['service']
    
    with pytest.raises(ValueError) as exc_info:
        service.enroll("S999", "C001")
    
    assert "Student not found" in str(exc_info.value)
```

**JavaScript (Jest):**
```javascript
test('Enrollment fails when student does not exist', () => {
    expect(() => {
        enrollmentService.enroll('S999', 'C001');
    }).toThrow('Student not found');
});
```

## 🔍 Common Test Patterns

### 1. Setup/Teardown

| Language | Setup | Cleanup |
|----------|-------|---------|
| Java | `@BeforeEach` | `@AfterEach` |
| Python | `@pytest.fixture` | (automatic) |
| JavaScript | `beforeEach()` | `afterEach()` |

### 2. Assertion Methods

**Java (JUnit 5):**
- `assertEquals(expected, actual)`
- `assertNotNull(object)`
- `assertTrue(condition)`
- `assertThrows(ExceptionClass, executable)`

**Python (Pytest):**
- `assert x == y`
- `assert x is not None`
- `assert condition`
- `with pytest.raises(Exception)`

**JavaScript (Jest):**
- `expect(x).toBe(y)`
- `expect(x).toBeDefined()`
- `expect(condition).toBeTruthy()`
- `expect(() => {}).toThrow()`

### 3. Testing Exceptions

**Java:**
```java
assertThrows(IllegalArgumentException.class, 
    () -> service.enroll("invalid", "invalid")
);
```

**Python:**
```python
with pytest.raises(ValueError):
    service.enroll("invalid", "invalid")
```

**JavaScript:**
```javascript
expect(() => {
    service.enroll("invalid", "invalid");
}).toThrow();
```

## 🎨 Code Organization Patterns

### Repository Pattern (In-Memory Storage)

**Java:**
```java
private Map<String, Student> students = new HashMap<>();

public Student findById(String id) {
    return students.get(id);
}
```

**Python:**
```python
def __init__(self):
    self.students = {}

def find_by_id(self, id):
    return self.students.get(id)
```

**JavaScript:**
```javascript
constructor() {
    this.students = {};
}

findById(id) {
    return this.students[id] || null;
}
```

## 🧪 Test Data Setup

### Typical Test Data

```
Students:
- S001: John Doe (max 18 credits)
- S002: Jane Smith (max 12 credits)

Courses:
- C001: Math 101 (3 credits)
- C002: Physics 101 (4 credits)
- C003: Chemistry 101 (3 credits)
```

### Setup Pattern

1. **Initialize repositories** (empty)
2. **Create service** with repository dependencies
3. **Add test data** (students and courses)
4. **Run test** with known data
5. **Verify results** using assertions

## 📈 Test Coverage Goals

- **Service Layer**: 100% coverage (all business logic)
- **Model Layer**: Basic validation coverage
- **Repository Layer**: CRUD operation coverage
- **Controller Layer**: Integration test coverage (optional)

## 🚨 Common Testing Mistakes to Avoid

1. ❌ **Testing implementation details** instead of behavior
2. ❌ **Sharing state between tests** (tests should be independent)
3. ❌ **Not testing error cases** (only happy path)
4. ❌ **Overly complex test setup** (keep it simple)
5. ❌ **Not using descriptive test names**
6. ❌ **Testing the framework** instead of your code

## ✅ Testing Best Practices

1. ✅ **Arrange-Act-Assert** pattern
2. ✅ **One assertion concept per test**
3. ✅ **Independent and repeatable tests**
4. ✅ **Fast execution** (use mocks for slow operations)
5. ✅ **Descriptive test names** (what, when, expected)
6. ✅ **Test edge cases and boundaries**

## 🎓 Study Tips

### For Java Students
- Learn JUnit 5 lifecycle annotations
- Understand `assertThrows()` for exception testing
- Practice with Maven for dependency management

### For Python Students
- Master pytest fixtures
- Use context managers (`with pytest.raises()`)
- Leverage pytest's simple assertion syntax

### For JavaScript Students
- Understand Jest's matcher system
- Use `describe()` blocks for organization
- Learn async testing patterns (for future use)

## 📚 Further Reading

### Java
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Framework](https://site.mockito.org/)

### Python
- [Pytest Documentation](https://docs.pytest.org/)
- [Python unittest module](https://docs.python.org/3/library/unittest.html)

### JavaScript
- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [Testing Best Practices](https://github.com/goldbergyoni/javascript-testing-best-practices)

---

**Remember**: Good tests are readable, maintainable, and trustworthy! 🎯
