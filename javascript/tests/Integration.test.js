const Student = require('../src/model/Student');
const Course = require('../src/model/Course');
const StudentRepository = require('../src/repository/StudentRepository');
const CourseRepository = require('../src/repository/CourseRepository');
const EnrollmentRepository = require('../src/repository/EnrollmentRepository');
const EnrollmentService = require('../src/service/EnrollmentService');
const EnrollmentController = require('../src/controller/EnrollmentController');

/**
 * Integration Tests - JavaScript
 * Testing full stack from Controller to Repository
 */

describe('Enrollment Integration Tests', () => {
    let controller;
    let service;
    let studentRepository;
    let courseRepository;
    let enrollmentRepository;

    beforeEach(() => {
        // Initialize all layers
        studentRepository = new StudentRepository();
        courseRepository = new CourseRepository();
        enrollmentRepository = new EnrollmentRepository();

        service = new EnrollmentService(
            studentRepository,
            courseRepository,
            enrollmentRepository
        );

        controller = new EnrollmentController(service);

        // Setup test data
        setupTestData();
    });

    function setupTestData() {
        studentRepository.save(new Student('S001', 'John Doe', 'john@example.com', 18));
        studentRepository.save(new Student('S002', 'Jane Smith', 'jane@example.com', 12));

        courseRepository.save(new Course('C001', 'Math 101', 3));
        courseRepository.save(new Course('C002', 'Physics 101', 4));
        courseRepository.save(new Course('C003', 'Chemistry 101', 3));
    }

    test('Integration: Complete enrollment flow - Success', () => {
        // Call controller
        const result = controller.enrollStudent('S001', 'C001');

        // Check controller response
        expect(result).toContain('SUCCESS');
        expect(result).toContain('S001');
        expect(result).toContain('C001');

        // Check service created enrollment
        const enrollment = enrollmentRepository.findByStudentAndCourse('S001', 'C001');
        expect(enrollment).toBeDefined();
        expect(enrollment.studentId).toBe('S001');
        expect(enrollment.courseId).toBe('C001');

        // Check repository updated student
        const student = studentRepository.findById('S001');
        expect(student.currentCredits).toBe(3);
    });

    test('Integration: Enrollment with invalid student', () => {
        const result = controller.enrollStudent('S999', 'C001');

        expect(result).toContain('ERROR');
        expect(result).toContain('Student not found');
    });

    test('Integration: Multiple enrollments and GPA calculation', () => {
        // Enroll in multiple courses
        const result1 = controller.enrollStudent('S001', 'C001');
        const result2 = controller.enrollStudent('S001', 'C002');

        expect(result1).toContain('SUCCESS');
        expect(result2).toContain('SUCCESS');

        // Set grades
        const e1 = enrollmentRepository.findByStudentAndCourse('S001', 'C001');
        const e2 = enrollmentRepository.findByStudentAndCourse('S001', 'C002');
        e1.setGrade(3.5);
        e2.setGrade(4.0);

        // Get GPA through controller
        const gpaResult = controller.getStudentGPA('S001');

        expect(gpaResult).toContain('GPA');
        expect(gpaResult).toMatch(/3\.78|3\.79/);

        // Verify credits
        const student = studentRepository.findById('S001');
        expect(student.currentCredits).toBe(7); // 3 + 4
    });

    test('Integration: Credit limit enforcement across layers', () => {
        // Student S002 has max 12 credits
        controller.enrollStudent('S002', 'C001'); // 3 credits
        controller.enrollStudent('S002', 'C002'); // 4 credits (total 7)

        // Add course that would exceed limit
        courseRepository.save(new Course('C004', 'Advanced Topics', 6));

        // Try to enroll
        const result = controller.enrollStudent('S002', 'C004');

        // Should fail
        expect(result).toContain('ERROR');
        expect(result).toContain('credit limit');

        // Verify credits didn't change
        const student = studentRepository.findById('S002');
        expect(student.currentCredits).toBe(7);

        // Verify enrollment wasn't created
        const enrollment = enrollmentRepository.findByStudentAndCourse('S002', 'C004');
        expect(enrollment).toBeNull();
    });

    test('Integration: End-to-end student lifecycle', () => {
        const studentId = 'S001';

        // Enroll in courses
        service.enroll(studentId, 'C001');
        expect(studentRepository.findById(studentId).currentCredits).toBe(3);

        service.enroll(studentId, 'C002');
        expect(studentRepository.findById(studentId).currentCredits).toBe(7);

        // Set grades
        enrollmentRepository.findByStudentAndCourse(studentId, 'C001').setGrade(3.0);
        enrollmentRepository.findByStudentAndCourse(studentId, 'C002').setGrade(4.0);

        // Calculate GPA
        const gpa = service.calculateGPA(studentId);

        // Verify
        expect(gpa).toBeCloseTo(3.57, 2); // (3*3 + 4*4) / 7 = 3.57
        expect(enrollmentRepository.findByStudentId(studentId)).toHaveLength(2);
    });

    test('Integration: Concurrent enrollments for different students', () => {
        // Enroll different students in same course
        const result1 = controller.enrollStudent('S001', 'C001');
        const result2 = controller.enrollStudent('S002', 'C001');

        // Both should succeed
        expect(result1).toContain('SUCCESS');
        expect(result2).toContain('SUCCESS');

        // Verify enrollments
        expect(enrollmentRepository.findByStudentAndCourse('S001', 'C001')).toBeDefined();
        expect(enrollmentRepository.findByStudentAndCourse('S002', 'C001')).toBeDefined();

        // Verify credits
        expect(studentRepository.findById('S001').currentCredits).toBe(3);
        expect(studentRepository.findById('S002').currentCredits).toBe(3);
    });

    test('Integration: Error handling propagates through layers', () => {
        // Test that errors from service layer reach controller
        const result = controller.enrollStudent('S001', 'C999');

        expect(result).toContain('ERROR');
        expect(result).toContain('Course not found');

        // Verify no enrollment was created
        const enrollments = enrollmentRepository.findByStudentId('S001');
        expect(enrollments).toHaveLength(0);
    });
});
