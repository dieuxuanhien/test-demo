const Student = require('../src/model/Student');
const Course = require('../src/model/Course');
const StudentRepository = require('../src/repository/StudentRepository');
const CourseRepository = require('../src/repository/CourseRepository');
const EnrollmentRepository = require('../src/repository/EnrollmentRepository');
const EnrollmentService = require('../src/service/EnrollmentService');

/**
 * Unit Tests for EnrollmentService
 * These tests focus on the Service layer business logic
 */
describe('EnrollmentService', () => {
    let enrollmentService;
    let studentRepository;
    let courseRepository;
    let enrollmentRepository;

    beforeEach(() => {
        // Initialize repositories
        studentRepository = new StudentRepository();
        courseRepository = new CourseRepository();
        enrollmentRepository = new EnrollmentRepository();

        // Initialize service
        enrollmentService = new EnrollmentService(
            studentRepository,
            courseRepository,
            enrollmentRepository
        );

        // Setup test data
        setupTestData();
    });

    function setupTestData() {
        // Create test students
        const student1 = new Student('S001', 'John Doe', 'john@example.com', 18);
        const student2 = new Student('S002', 'Jane Smith', 'jane@example.com', 12);
        studentRepository.save(student1);
        studentRepository.save(student2);

        // Create test courses
        const course1 = new Course('C001', 'Math 101', 3);
        const course2 = new Course('C002', 'Physics 101', 4);
        const course3 = new Course('C003', 'Chemistry 101', 3);
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
    }

    /**
     * Test Case 1: Student does not exist
     * Expected: Should throw Error
     */
    test('Test 1: Enrollment fails when student does not exist', () => {
        // Arrange
        const nonExistentStudentId = 'S999';
        const validCourseId = 'C001';

        // Act & Assert
        expect(() => {
            enrollmentService.enroll(nonExistentStudentId, validCourseId);
        }).toThrow('Student not found');

        console.log('✓ Test 1 passed: Student not found error thrown');
    });

    /**
     * Test Case 2: Course does not exist
     * Expected: Should throw Error
     */
    test('Test 2: Enrollment fails when course does not exist', () => {
        // Arrange
        const validStudentId = 'S001';
        const nonExistentCourseId = 'C999';

        // Act & Assert
        expect(() => {
            enrollmentService.enroll(validStudentId, nonExistentCourseId);
        }).toThrow('Course not found');

        console.log('✓ Test 2 passed: Course not found error thrown');
    });

    /**
     * Test Case 3: Student exceeds credit limit
     * Expected: Should throw Error
     */
    test('Test 3: Enrollment fails when student exceeds credit limit', () => {
        // Arrange
        const studentId = 'S002'; // Has max 12 credits
        const courseId1 = 'C001'; // 3 credits
        const courseId2 = 'C002'; // 4 credits

        // Enroll in first two courses (3 + 4 = 7 credits)
        enrollmentService.enroll(studentId, courseId1);
        enrollmentService.enroll(studentId, courseId2);

        // Create a new course that would exceed limit
        const largeCourse = new Course('C004', 'Advanced Topics', 6);
        courseRepository.save(largeCourse);

        // Act & Assert - trying to add 6 more credits (total would be 13 > 12)
        expect(() => {
            enrollmentService.enroll(studentId, 'C004');
        }).toThrow('exceeds maximum credit limit');

        console.log('✓ Test 3 passed: Credit limit exceeded error thrown');
    });

    /**
     * Test Case 4: Successful enrollment
     * Expected: Enrollment created, student credits updated
     */
    test('Test 4: Successful enrollment when all conditions are met', () => {
        // Arrange
        const studentId = 'S001';
        const courseId = 'C001';
        const student = studentRepository.findById(studentId);
        const course = courseRepository.findById(courseId);
        const initialCredits = student.currentCredits;

        // Act
        const enrollment = enrollmentService.enroll(studentId, courseId);

        // Assert
        expect(enrollment).toBeDefined();
        expect(enrollment.studentId).toBe(studentId);
        expect(enrollment.courseId).toBe(courseId);

        // Verify student credits were updated
        const updatedStudent = studentRepository.findById(studentId);
        expect(updatedStudent.currentCredits).toBe(initialCredits + course.credits);

        console.log('✓ Test 4 passed: Enrollment successful');
        console.log(`  Student credits updated: ${initialCredits} → ${updatedStudent.currentCredits}`);
    });

    /**
     * Bonus Test: Calculate GPA
     */
    test('Bonus: Calculate GPA correctly', () => {
        // Arrange
        const studentId = 'S001';

        // Enroll in courses
        const e1 = enrollmentService.enroll(studentId, 'C001'); // 3 credits
        const e2 = enrollmentService.enroll(studentId, 'C002'); // 4 credits

        // Set grades
        e1.setGrade(3.5); // Math: 3.5 GPA, 3 credits
        e2.setGrade(4.0); // Physics: 4.0 GPA, 4 credits

        // Act
        const gpa = enrollmentService.calculateGPA(studentId);

        // Assert
        // Expected GPA = (3.5*3 + 4.0*4) / (3+4) = (10.5 + 16) / 7 = 26.5 / 7 = 3.785...
        expect(gpa).toBeCloseTo(3.785, 2);
        console.log(`✓ Bonus test passed: GPA = ${gpa.toFixed(2)}`);
    });
});
