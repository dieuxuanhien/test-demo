/**
 * INTEGRATION TESTS for Enrollment System (JavaScript)
 * 
 * This test suite demonstrates INTEGRATION TESTING:
 * - Tests the COMPLETE FLOW from Controller → Service → Repository
 * - Uses REAL OBJECTS (no mocks/stubs)
 * - Verifies that all layers work together correctly
 * - Tests end-to-end business scenarios
 * 
 * Key Differences from Unit Tests:
 * 1. NO MOCKS - All dependencies are real implementations
 * 2. Tests multiple components working together
 * 3. Validates the integration between layers
 * 4. Uses in-memory repositories (simulating real database behavior)
 */

const EnrollmentController = require('../controller/EnrollmentController');
const EnrollmentService = require('../service/EnrollmentService');
const StudentRepository = require('../repository/StudentRepository');
const CourseRepository = require('../repository/CourseRepository');
const EnrollmentRepository = require('../repository/EnrollmentRepository');
const Student = require('../model/Student');
const Course = require('../model/Course');

describe('Enrollment System - Integration Tests', () => {
    // Real instances - NO MOCKS!
    let studentRepository;
    let courseRepository;
    let enrollmentRepository;
    let enrollmentService;
    let enrollmentController;

    beforeEach(() => {
        // Create fresh instances for each test (no mocking framework needed)
        studentRepository = new StudentRepository();
        courseRepository = new CourseRepository();
        enrollmentRepository = new EnrollmentRepository();

        // Wire up the real dependencies
        enrollmentService = new EnrollmentService(
            studentRepository,
            courseRepository,
            enrollmentRepository
        );

        enrollmentController = new EnrollmentController(enrollmentService);

        // Set up test data in repositories
        setupTestData();
    });

    /**
     * Initialize repositories with test data
     * This simulates what would normally be in a database
     */
    function setupTestData() {
        // Add students
        const student1 = new Student('S001', 'Alice Johnson', 'alice@example.com', 18);
        const student2 = new Student('S002', 'Bob Smith', 'bob@example.com', 15);
        studentRepository.save(student1);
        studentRepository.save(student2);

        // Add courses
        const course1 = new Course('C001', 'Java Programming', 3);
        const course2 = new Course('C002', 'Data Structures', 4);
        const course3 = new Course('C003', 'Web Development', 3);
        const course4 = new Course('C004', 'Machine Learning', 4);
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
    }

    // ==================== INTEGRATION TEST SCENARIOS ====================

    describe('Complete enrollment flow', () => {
        test('should successfully enroll student through all layers', () => {
            /**
             * SCENARIO: Student successfully enrolls in a course
             * FLOW: Controller → Service → Repositories
             * VALIDATES: All layers integrate correctly for happy path
             */

            // ACT - Call through the controller (top layer)
            const result = enrollmentController.enrollStudent('S001', 'C001');

            // ASSERT - Verify response from controller
            expect(result).toContain('SUCCESS');
            expect(result).toContain('S001');
            expect(result).toContain('C001');

            // VERIFY - Check that data was persisted correctly in repository
            const savedEnrollment = enrollmentRepository.findByStudentAndCourse('S001', 'C001');
            expect(savedEnrollment).toBeDefined();
            expect(savedEnrollment.studentId).toBe('S001');
            expect(savedEnrollment.courseId).toBe('C001');

            // VERIFY - Student credits were updated
            const student = studentRepository.findById('S001');
            expect(student.currentCredits).toBe(3);
        });

        test('should handle multiple enrollments with credit accumulation', () => {
            /**
             * SCENARIO: Student enrolls in multiple courses
             * VALIDATES: Credits accumulate correctly across multiple transactions
             */

            // ACT - Enroll in three courses
            enrollmentService.enroll('S001', 'C001'); // 3 credits
            enrollmentService.enroll('S001', 'C002'); // 4 credits
            enrollmentService.enroll('S001', 'C003'); // 3 credits

            // ASSERT - Verify total credits
            const student = studentRepository.findById('S001');
            expect(student.currentCredits).toBe(10); // 3+4+3

            // VERIFY - All enrollments were saved
            const enrollments = enrollmentRepository.findByStudentId('S001');
            expect(enrollments).toHaveLength(3);
        });
    });

    describe('Business rule enforcement', () => {
        test('should prevent enrollment when credit limit would be exceeded', () => {
            /**
             * SCENARIO: Student attempts to exceed credit limit
             * VALIDATES: Business rules are enforced across layers
             */

            // ARRANGE - Student S002 has max 15 credits
            enrollmentService.enroll('S002', 'C001'); // 3 credits (total: 3)
            enrollmentService.enroll('S002', 'C002'); // 4 credits (total: 7)
            enrollmentService.enroll('S002', 'C003'); // 3 credits (total: 10)
            enrollmentService.enroll('S002', 'C004'); // 4 credits (total: 14)

            // VERIFY - Student has 14 credits
            const student = studentRepository.findById('S002');
            expect(student.currentCredits).toBe(14);

            // Try to add another 4-credit course (would exceed 15)
            const newCourse = new Course('C005', 'Advanced Topics', 4);
            courseRepository.save(newCourse);

            // ACT & ASSERT - Should throw error
            expect(() => {
                enrollmentService.enroll('S002', 'C005');
            }).toThrow('Student exceeds maximum credit limit');

            // VERIFY - Credits didn't change
            const studentAfter = studentRepository.findById('S002');
            expect(studentAfter.currentCredits).toBe(14);
        });

        test('should reject enrollment for non-existent student', () => {
            /**
             * SCENARIO: Attempt to enroll non-existent student
             * VALIDATES: Error handling across all layers
             */

            // ACT - Try to enroll non-existent student through controller
            const result = enrollmentController.enrollStudent('S999', 'C001');

            // ASSERT - Controller should return error message
            expect(result).toContain('ERROR');
            expect(result).toContain('Student not found');

            // VERIFY - No enrollment was created
            const enrollments = enrollmentRepository.findByStudentId('S999');
            expect(enrollments).toHaveLength(0);
        });

        test('should reject enrollment for non-existent course', () => {
            // ACT
            const result = enrollmentController.enrollStudent('S001', 'C999');

            // ASSERT
            expect(result).toContain('ERROR');
            expect(result).toContain('Course not found');
        });
    });

    describe('GPA calculation integration', () => {
        test('should calculate GPA correctly with real enrollment data', () => {
            /**
             * SCENARIO: Calculate GPA after multiple enrollments with grades
             * VALIDATES: Complete workflow including enrollment, grade assignment, and GPA calculation
             */

            // ARRANGE - Enroll student in courses
            const enrollment1 = enrollmentService.enroll('S001', 'C001');
            const enrollment2 = enrollmentService.enroll('S001', 'C002');
            const enrollment3 = enrollmentService.enroll('S001', 'C003');

            // Set grades (simulate grading after semester)
            enrollment1.grade = 4.0; // A in Java (3 credits)
            enrollment2.grade = 3.5; // B+ in Data Structures (4 credits)
            enrollment3.grade = 3.0; // B in Web Dev (3 credits)

            // ACT - Calculate GPA through service
            const gpa = enrollmentService.calculateGPA('S001');

            // ASSERT - Verify GPA calculation
            // Expected: (4.0*3 + 3.5*4 + 3.0*3) / (3+4+3) = (12+14+9)/10 = 3.5
            expect(gpa).toBeCloseTo(3.5, 2);

            // VERIFY through controller
            const gpaResult = enrollmentController.getStudentGPA('S001');
            expect(gpaResult).toContain('3.50');
        });

        test('should return 0.0 GPA for student with no enrollments', () => {
            /**
             * SCENARIO: Calculate GPA for student with no enrollments
             * VALIDATES: Edge case handling across layers
             */

            // ACT
            const gpa = enrollmentService.calculateGPA('S001');

            // ASSERT
            expect(gpa).toBe(0.0);

            // VERIFY through controller
            const result = enrollmentController.getStudentGPA('S001');
            expect(result).toContain('0.00');
        });
    });

    describe('Multi-student scenarios', () => {
        test('should allow multiple students to enroll in same course', () => {
            /**
             * SCENARIO: Different students enrolling in the same course
             * VALIDATES: Repository correctly handles multiple enrollments
             */

            // ACT - Two different students enroll in the same course
            const enrollment1 = enrollmentService.enroll('S001', 'C001');
            const enrollment2 = enrollmentService.enroll('S002', 'C001');

            // ASSERT - Both enrollments are independent
            expect(enrollment1.studentId).toBe('S001');
            expect(enrollment2.studentId).toBe('S002');
            expect(enrollment1.courseId).toBe('C001');
            expect(enrollment2.courseId).toBe('C001');

            // VERIFY - Each student's credits updated correctly
            const student1 = studentRepository.findById('S001');
            const student2 = studentRepository.findById('S002');
            expect(student1.currentCredits).toBe(3);
            expect(student2.currentCredits).toBe(3);
        });
    });

    describe('Data persistence', () => {
        test('should maintain state across multiple service calls', () => {
            /**
             * SCENARIO: Verify that data persists across multiple service calls
             * VALIDATES: Repository layer correctly maintains state
             */

            // ACT - Perform operations
            enrollmentService.enroll('S001', 'C001');

            // VERIFY - Data persists for subsequent calls
            let student = studentRepository.findById('S001');
            expect(student.currentCredits).toBe(3);

            // ACT - Enroll in another course
            enrollmentService.enroll('S001', 'C002');

            // VERIFY - Previous data is still there
            student = studentRepository.findById('S001');
            expect(student.currentCredits).toBe(7); // 3+4

            const enrollments = enrollmentRepository.findByStudentId('S001');
            expect(enrollments).toHaveLength(2);
        });

        test('should isolate data between different test runs', () => {
            // This test verifies that beforeEach creates fresh instances
            const enrollments = enrollmentRepository.findByStudentId('S001');
            expect(enrollments).toHaveLength(0);

            const student = studentRepository.findById('S001');
            expect(student.currentCredits).toBe(0);
        });
    });
});
