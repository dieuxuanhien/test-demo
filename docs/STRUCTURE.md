# Complete Project Structure

```
demo-unit-integration-test/
│
├── 📋 README.md                          # Main project documentation
├── 🚫 .gitignore                         # Git ignore rules
│
├── 📚 docs/                              # Documentation folder
│   ├── class-diagram.puml                # PlantUML class diagram
│   ├── QUICK_REFERENCE.md                # Command & syntax reference
│   ├── SEMINAR_OUTLINE.md                # Presentation guide (60 min)
│   ├── PROJECT_SUMMARY.md                # Project statistics & overview
│   └── STRUCTURE.md                      # This file
│
├── ☕ java/                               # Java implementation
│   ├── README.md                         # Java-specific guide
│   ├── pom.xml                          # Maven configuration
│   │
│   └── src/
│       ├── main/java/com/demo/
│       │   │
│       │   ├── model/                   # 📦 Model Layer
│       │   │   ├── Student.java         # - Student entity
│       │   │   ├── Course.java          # - Course entity
│       │   │   └── Enrollment.java      # - Enrollment entity
│       │   │
│       │   ├── repository/              # 💾 Repository Layer
│       │   │   ├── StudentRepository.java
│       │   │   ├── CourseRepository.java
│       │   │   └── EnrollmentRepository.java
│       │   │
│       │   ├── service/                 # ⚙️  Service Layer (TESTING FOCUS)
│       │   │   └── EnrollmentService.java
│       │   │       ├── enroll()         # Business logic: enrollment
│       │   │       └── calculateGPA()   # Business logic: GPA
│       │   │
│       │   └── controller/              # 🎮 Controller Layer
│       │       └── EnrollmentController.java
│       │
│       └── test/java/com/demo/
│           └── service/
│               └── EnrollmentServiceTest.java  # ✅ JUnit 5 Tests
│                   ├── Test 1: Student not found
│                   ├── Test 2: Course not found
│                   ├── Test 3: Credit limit exceeded
│                   ├── Test 4: Successful enrollment
│                   └── Bonus: GPA calculation
│
├── 🐍 python/                            # Python implementation
│   ├── README.md                        # Python-specific guide
│   ├── requirements.txt                 # Python dependencies
│   │
│   ├── src/
│   │   ├── __init__.py
│   │   │
│   │   ├── model/                       # 📦 Model Layer
│   │   │   ├── __init__.py
│   │   │   ├── student.py               # - Student entity
│   │   │   ├── course.py                # - Course entity
│   │   │   └── enrollment.py            # - Enrollment entity
│   │   │
│   │   ├── repository/                  # 💾 Repository Layer
│   │   │   ├── __init__.py
│   │   │   ├── student_repository.py
│   │   │   ├── course_repository.py
│   │   │   └── enrollment_repository.py
│   │   │
│   │   ├── service/                     # ⚙️  Service Layer (TESTING FOCUS)
│   │   │   ├── __init__.py
│   │   │   └── enrollment_service.py
│   │   │       ├── enroll()             # Business logic: enrollment
│   │   │       └── calculate_gpa()      # Business logic: GPA
│   │   │
│   │   └── controller/                  # 🎮 Controller Layer
│   │       ├── __init__.py
│   │       └── enrollment_controller.py
│   │
│   └── tests/
│       ├── __init__.py
│       └── test_enrollment_service.py   # ✅ Pytest Tests
│           ├── Test 1: Student not found
│           ├── Test 2: Course not found
│           ├── Test 3: Credit limit exceeded
│           ├── Test 4: Successful enrollment
│           └── Bonus: GPA calculation
│
└── 📜 javascript/                        # JavaScript implementation
    ├── README.md                        # JavaScript-specific guide
    ├── package.json                     # npm configuration
    │
    ├── src/
    │   │
    │   ├── model/                       # 📦 Model Layer
    │   │   ├── Student.js               # - Student entity
    │   │   ├── Course.js                # - Course entity
    │   │   └── Enrollment.js            # - Enrollment entity
    │   │
    │   ├── repository/                  # 💾 Repository Layer
    │   │   ├── StudentRepository.js
    │   │   ├── CourseRepository.js
    │   │   └── EnrollmentRepository.js
    │   │
    │   ├── service/                     # ⚙️  Service Layer (TESTING FOCUS)
    │   │   └── EnrollmentService.js
    │   │       ├── enroll()             # Business logic: enrollment
    │   │       └── calculateGPA()       # Business logic: GPA
    │   │
    │   └── controller/                  # 🎮 Controller Layer
    │       └── EnrollmentController.js
    │
    └── tests/
        └── EnrollmentService.test.js    # ✅ Jest Tests
            ├── Test 1: Student not found
            ├── Test 2: Course not found
            ├── Test 3: Credit limit exceeded
            ├── Test 4: Successful enrollment
            └── Bonus: GPA calculation
```

---

## 📊 Layer Distribution

### Model Layer (Data Entities)
- **Purpose**: Define data structures
- **Files**: 3 per language × 3 languages = **9 files**
- **Classes**: Student, Course, Enrollment

### Repository Layer (Data Access)
- **Purpose**: Manage in-memory storage
- **Files**: 3 per language × 3 languages = **9 files**
- **Implementation**: Map/Dict/Object for storage

### Service Layer (Business Logic) ⭐ TEST FOCUS
- **Purpose**: Implement business rules
- **Files**: 1 per language × 3 languages = **3 files**
- **Methods**: 
  - `enroll(studentId, courseId)` - Enrollment logic
  - `calculateGPA(studentId)` - GPA calculation

### Controller Layer (Request Handling)
- **Purpose**: Handle requests, call service
- **Files**: 1 per language × 3 languages = **3 files**
- **Methods**: 
  - `enrollStudent()` - Enrollment endpoint
  - `getStudentGPA()` - GPA endpoint

### Test Layer
- **Purpose**: Verify business logic
- **Files**: 1 per language × 3 languages = **3 files**
- **Test Cases**: 5 per language × 3 languages = **15 tests**

---

## 🎯 Testing Strategy

```
Layer Architecture:
┌──────────────────────────────────────┐
│  Controller (EnrollmentController)   │ ← Integration Tests (optional)
├──────────────────────────────────────┤
│  Service (EnrollmentService)         │ ← ⭐ UNIT TESTS (our focus)
├──────────────────────────────────────┤
│  Repository (Student/Course/Enroll)  │ ← Tested indirectly
├──────────────────────────────────────┤
│  Model (Student/Course/Enrollment)   │ ← Tested indirectly
└──────────────────────────────────────┘

Test Pyramid:
     /\
    /  \      E2E Tests (few)
   /────\
  /      \    Integration Tests (some)
 /────────\
/__________\  Unit Tests (many) ← We are here!
```

---

## 📈 File Count Summary

| Category | Count | Notes |
|----------|-------|-------|
| **Documentation** | 6 | README files, guides, diagrams |
| **Java Source** | 8 | Model (3) + Repo (3) + Service (1) + Controller (1) |
| **Java Tests** | 1 | EnrollmentServiceTest.java |
| **Java Config** | 2 | pom.xml + README |
| **Python Source** | 8 | Model (3) + Repo (3) + Service (1) + Controller (1) |
| **Python Tests** | 1 | test_enrollment_service.py |
| **Python Config** | 9 | __init__.py (7) + requirements.txt + README |
| **JS Source** | 8 | Model (3) + Repo (3) + Service (1) + Controller (1) |
| **JS Tests** | 1 | EnrollmentService.test.js |
| **JS Config** | 2 | package.json + README |
| **Other** | 1 | .gitignore |
| **TOTAL** | **47 files** | |

---

## 🚀 Quick Navigation

### Want to understand the architecture?
→ See `docs/class-diagram.puml`

### Want to run tests?
→ Check language-specific `README.md` files

### Want to present this?
→ Follow `docs/SEMINAR_OUTLINE.md`

### Want quick commands?
→ Use `docs/QUICK_REFERENCE.md`

### Want project overview?
→ Read main `README.md`

### Want detailed stats?
→ Review `docs/PROJECT_SUMMARY.md`

---

## 🎓 Learning Path

**Beginner:**
1. Start with `README.md` - understand the concept
2. Look at `class-diagram.puml` - visualize architecture
3. Pick one language (your comfort zone)
4. Read that language's code files
5. Run the tests and see them pass

**Intermediate:**
1. Compare service implementations across languages
2. Study test files and testing patterns
3. Try modifying a test to make it fail
4. Add a new test case
5. Compare testing frameworks

**Advanced:**
1. Add a new feature (e.g., unenroll method)
2. Write tests first (TDD approach)
3. Implement mocking in tests
4. Create integration tests
5. Add a new entity (e.g., Professor)

---

## 💡 Key Files for Demo

**Live Coding:**
- `java/src/main/java/com/demo/service/EnrollmentService.java`
- `java/src/test/java/com/demo/service/EnrollmentServiceTest.java`

**Architecture Explanation:**
- `docs/class-diagram.puml`
- Main `README.md`

**Language Comparison:**
- Compare Service implementations side-by-side
- Compare Test files side-by-side

---

**This structure enables:**
✅ Easy navigation
✅ Language comparison
✅ Clear learning path
✅ Self-contained examples
✅ Extensibility

**Each language folder is independent and runnable!**
