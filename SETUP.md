# Setup Guide - Testing Demo Project

## 🎯 Prerequisites

Before running this project, ensure you have the following installed:

### For All Languages
- Git (to clone the repository)
- A text editor or IDE (VS Code, IntelliJ IDEA, PyCharm, etc.)

### For Java
- **Java Development Kit (JDK)** 11 or higher
  - Download: https://adoptium.net/
  - Verify: `java -version` and `javac -version`
- **Apache Maven** 3.6 or higher
  - Download: https://maven.apache.org/download.cgi
  - Verify: `mvn -version`

### For Python
- **Python** 3.7 or higher
  - Download: https://www.python.org/downloads/
  - Verify: `python3 --version`
- **pip** (Python package manager)
  - Usually comes with Python
  - Verify: `pip3 --version`

### For JavaScript
- **Node.js** 14 or higher (includes npm)
  - Download: https://nodejs.org/
  - Verify: `node --version` and `npm --version`

---

## 📦 Installation Steps

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd demo-unit-integration-test
```

### Step 2: Set Up Java Project
```bash
cd java

# Install dependencies and compile
mvn clean install

# Run tests
mvn test

# Return to root
cd ..
```

**Expected Output:**
```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Step 3: Set Up Python Project
```bash
cd python

# Install dependencies
pip3 install -r requirements.txt

# Run tests
pytest tests/ -v

# Return to root
cd ..
```

**Expected Output:**
```
test_enrollment_service.py::TestEnrollmentService::test_enrollment_student_not_found PASSED
test_enrollment_service.py::TestEnrollmentService::test_enrollment_course_not_found PASSED
test_enrollment_service.py::TestEnrollmentService::test_enrollment_exceeds_credit_limit PASSED
test_enrollment_service.py::TestEnrollmentService::test_enrollment_success PASSED
test_enrollment_service.py::TestEnrollmentService::test_calculate_gpa PASSED

====== 5 passed in 0.05s ======
```

### Step 4: Set Up JavaScript Project
```bash
cd javascript

# Install dependencies
npm install

# Run tests
npm test

# Return to root
cd ..
```

**Expected Output:**
```
PASS tests/EnrollmentService.test.js
  EnrollmentService
    ✓ Test 1: Enrollment fails when student does not exist (3 ms)
    ✓ Test 2: Enrollment fails when course does not exist (1 ms)
    ✓ Test 3: Enrollment fails when student exceeds credit limit (2 ms)
    ✓ Test 4: Successful enrollment when all conditions are met (1 ms)
    ✓ Bonus: Calculate GPA correctly (1 ms)

Tests: 5 passed, 5 total
```

---

## ✅ Verification

### Automated Verification (Linux/Mac)
```bash
chmod +x verify-setup.sh
./verify-setup.sh
```

### Manual Verification

**Check Java:**
```bash
java -version    # Should show Java 11+
javac -version   # Should show Java 11+
mvn -version     # Should show Maven 3.6+
cd java && mvn test
```

**Check Python:**
```bash
python3 --version      # Should show Python 3.7+
pip3 --version         # Should show pip
cd python && pytest tests/ -v
```

**Check JavaScript:**
```bash
node --version         # Should show Node 14+
npm --version          # Should show npm
cd javascript && npm test
```

---

## 🚨 Troubleshooting

### Java Issues

**Problem:** `mvn: command not found`
- **Solution:** Install Maven from https://maven.apache.org/install.html
- Add Maven to your PATH

**Problem:** Tests fail with compilation errors
- **Solution:** Check Java version is 11+
- Run `mvn clean install` to rebuild

**Problem:** `JAVA_HOME not set`
- **Solution:** Set JAVA_HOME environment variable
  ```bash
  # Linux/Mac
  export JAVA_HOME=/path/to/jdk
  
  # Windows
  set JAVA_HOME=C:\Path\To\JDK
  ```

### Python Issues

**Problem:** `pytest: command not found`
- **Solution:** Install pytest
  ```bash
  pip3 install pytest
  # or
  pip3 install -r requirements.txt
  ```

**Problem:** `ModuleNotFoundError: No module named 'src'`
- **Solution:** Run pytest from the python directory
  ```bash
  cd python
  pytest tests/ -v
  ```

**Problem:** Import errors in tests
- **Solution:** Ensure all `__init__.py` files exist in package directories

### JavaScript Issues

**Problem:** `npm: command not found`
- **Solution:** Install Node.js from https://nodejs.org/

**Problem:** `Cannot find module 'jest'`
- **Solution:** Install dependencies
  ```bash
  cd javascript
  npm install
  ```

**Problem:** Tests fail with module errors
- **Solution:** Ensure you're using CommonJS (not ES modules)
- Check that all `module.exports` and `require()` statements are correct

---

## 🔧 IDE Setup

### VS Code
```bash
# Install recommended extensions
code --install-extension vscjava.vscode-java-pack
code --install-extension ms-python.python
code --install-extension dbaeumer.vscode-eslint
```

### IntelliJ IDEA
1. Open `java` folder as a Maven project
2. Enable auto-import for Maven dependencies
3. Run tests using built-in test runner

### PyCharm
1. Open `python` folder as a project
2. Configure Python interpreter (Python 3.7+)
3. Install requirements from `requirements.txt`
4. Run tests using built-in pytest runner

---

## 📊 Project Structure Verification

After setup, your directory should look like:
```
demo-unit-integration-test/
├── docs/
├── java/
│   ├── src/
│   ├── target/        ← Created after mvn test
│   └── pom.xml
├── python/
│   ├── src/
│   ├── tests/
│   ├── __pycache__/   ← Created after running tests
│   └── requirements.txt
├── javascript/
│   ├── src/
│   ├── tests/
│   ├── node_modules/  ← Created after npm install
│   └── package.json
└── README.md
```

---

## 🎓 Next Steps

Once everything is set up:

1. **Read the documentation**
   - Start with main `README.md`
   - Review `docs/SEMINAR_OUTLINE.md` if presenting

2. **Explore the code**
   - Compare implementations across languages
   - Study the test files

3. **Run tests individually**
   - Understand what each test does
   - Try modifying tests to see them fail

4. **Experiment**
   - Add new test cases
   - Implement new features
   - Try different testing approaches

---

## 📞 Getting Help

### Documentation
- Main README: `./README.md`
- Quick Reference: `./docs/QUICK_REFERENCE.md`
- Seminar Guide: `./docs/SEMINAR_OUTLINE.md`
- Project Summary: `./docs/PROJECT_SUMMARY.md`

### Testing Framework Docs
- JUnit 5: https://junit.org/junit5/docs/current/user-guide/
- Pytest: https://docs.pytest.org/
- Jest: https://jestjs.io/docs/getting-started

### Language-Specific Help
- Each language folder has its own `README.md`
- Check stack traces for specific errors
- Review test output for clues

---

## ✨ You're Ready!

If all tests pass, you're ready to:
- 🎤 Present the seminar
- 📚 Study the code
- 🧪 Practice testing
- 🔧 Extend the project

**Happy Testing!** 🎉
