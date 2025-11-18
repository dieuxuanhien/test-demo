# Testing Layer by Layer - Seminar Demo Project

A comprehensive demonstration project showing how to test business logic layer by layer (Controller → Service → Repository → Model) across three programming languages: **Java**, **Python**, and **JavaScript**.

## 📚 Project Overview

This project demonstrates:
- **Layered Architecture**: Clear separation between Model, Repository, Service, and Controller layers
- **Unit Testing**: Focus on testing the Service layer business logic
- **Consistent Design**: Same structure and naming conventions across all three languages
- **Test-Driven Development**: 4 core test cases covering different scenarios

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│       Controller Layer              │  ← Handles requests
│  (EnrollmentController)             │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       Service Layer                 │  ← Business Logic (TESTING FOCUS)
│  (EnrollmentService)                │
│  - enroll()                         │
│  - calculateGPA()                   │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       Repository Layer              │  ← Data Access (In-Memory)
│  - StudentRepository                │
│  - CourseRepository                 │
│  - EnrollmentRepository             │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│       Model Layer                   │  ← Data Entities
│  - Student                          │
│  - Course                           │
│  - Enrollment                       │
└─────────────────────────────────────┘
```

## 📋 Business Rules (Service Layer)

### Enrollment Logic
1. ✅ Student must exist in the system
2. ✅ Course must exist in the system
3. ✅ Student must not exceed maximum credit limit
4. ✅ Create enrollment and update student credits

### GPA Calculation
- Calculate weighted average: `GPA = Σ(grade × credits) / Σ(credits)`

## 🧪 Test Cases

All three implementations include identical test scenarios:

| Test # | Scenario | Expected Result |
|--------|----------|----------------|
| 1 | Student does not exist | Throw exception: "Student not found" |
| 2 | Course does not exist | Throw exception: "Course not found" |
| 3 | Student exceeds credit limit | Throw exception: "Exceeds maximum credit limit" |
| 4 | Valid enrollment | Success: Enrollment created, credits updated |
| Bonus | Calculate GPA | Correctly compute weighted average GPA |

## 🚀 Quick Start

### Java
```bash
cd java
mvn test
```

### Python
```bash
cd python
pip install -r requirements.txt
pytest tests/ -v
```

### JavaScript
```bash
cd javascript
npm install
npm test
```

## 📁 Project Structure

```
demo-unit-integration-test/
├── docs/
│   └── class-diagram.puml          # PlantUML class diagram
├── java/
│   ├── src/
│   │   ├── main/java/com/demo/
│   │   │   ├── model/              # Student, Course, Enrollment
│   │   │   ├── repository/         # Data access layer
│   │   │   ├── service/            # Business logic ⭐
│   │   │   └── controller/         # Request handlers
│   │   └── test/java/com/demo/
│   │       └── service/
│   │           └── EnrollmentServiceTest.java
│   ├── pom.xml
│   └── README.md
├── python/
│   ├── src/
│   │   ├── model/                  # student.py, course.py, enrollment.py
│   │   ├── repository/             # Data access layer
│   │   ├── service/                # Business logic ⭐
│   │   └── controller/             # Request handlers
│   ├── tests/
│   │   └── test_enrollment_service.py
│   ├── requirements.txt
│   └── README.md
├── javascript/
│   ├── src/
│   │   ├── model/                  # Student.js, Course.js, Enrollment.js
│   │   ├── repository/             # Data access layer
│   │   ├── service/                # Business logic ⭐
│   │   └── controller/             # Request handlers
│   ├── tests/
│   │   └── EnrollmentService.test.js
│   ├── package.json
│   └── README.md
└── README.md (this file)
```

## 🎯 Learning Objectives

### For Students
1. **Understand Layered Architecture**: See how responsibilities are separated across layers
2. **Learn Unit Testing**: Focus on testing business logic in isolation
3. **Compare Languages**: Observe similarities and differences in testing approaches
4. **Best Practices**: Exception handling, test organization, and assertions

### Key Takeaways
- ✅ Test business logic separately from data access and UI
- ✅ Use dependency injection for easier testing
- ✅ Write descriptive test names that explain the scenario
- ✅ Cover both happy paths and error conditions
- ✅ Keep tests independent and repeatable

## 🛠️ Technologies Used

### Java
- **Testing Framework**: JUnit 5
- **Build Tool**: Maven
- **Java Version**: 11+

### Python
- **Testing Framework**: Pytest
- **Package Manager**: pip
- **Python Version**: 3.7+

### JavaScript
- **Testing Framework**: Jest
- **Runtime**: Node.js
- **Package Manager**: npm

## 📊 UML Diagram

A complete PlantUML class diagram is available in `docs/class-diagram.puml` showing:
- All four layers (Model, Repository, Service, Controller)
- Class properties and methods
- Relationships and dependencies
- Business logic notes

To view the diagram, use a PlantUML viewer or visit [PlantUML Online](http://www.plantuml.com/plantuml/).

## 💡 Usage Examples

### Running Individual Test Files

**Java:**
```bash
mvn test -Dtest=EnrollmentServiceTest
```

**Python:**
```bash
pytest tests/test_enrollment_service.py -v
```

**JavaScript:**
```bash
npm test -- EnrollmentService.test.js
```

### With Coverage Reports

**Java:**
```bash
mvn test jacoco:report
```

**Python:**
```bash
pytest tests/ --cov=src --cov-report=html
```

**JavaScript:**
```bash
npm run test:coverage
```

## 🔍 Code Highlights

### Service Layer Example (Java)
```java
public Enrollment enroll(String studentId, String courseId) {
    // Business rule validation
    Student student = studentRepository.findById(studentId);
    if (student == null) {
        throw new IllegalArgumentException("Student not found");
    }
    
    Course course = courseRepository.findById(courseId);
    if (course == null) {
        throw new IllegalArgumentException("Course not found");
    }
    
    if (student.getCurrentCredits() + course.getCredits() > student.getMaxCredits()) {
        throw new IllegalStateException("Exceeds credit limit");
    }
    
    // Create enrollment
    Enrollment enrollment = new Enrollment(studentId, courseId);
    enrollmentRepository.save(enrollment);
    student.addCredits(course.getCredits());
    
    return enrollment;
}
```

### Test Example (Python)
```python
def test_enrollment_student_not_found(self, setup):
    """Test Case 1: Student does not exist"""
    service = setup['service']
    
    with pytest.raises(ValueError) as exc_info:
        service.enroll("S999", "C001")
    
    assert "Student not found" in str(exc_info.value)
```

## 📝 Notes for Seminar Presentation

1. **Start with Architecture**: Show the layered diagram first
2. **Explain Business Rules**: Why we test the Service layer
3. **Live Demo**: Run tests in one language
4. **Compare Approaches**: Show how testing differs across languages
5. **Q&A**: Common testing pitfalls and best practices

## 🤝 Contributing

This is a demo project for educational purposes. Feel free to:
- Add more test cases
- Implement additional features (e.g., unenroll, update grades)
- Add integration tests
- Create mocking examples

## 📄 License

This project is open source and available for educational use.

## 👥 Author

Created for software testing seminar demonstration.

---

**Happy Testing! 🎉**
