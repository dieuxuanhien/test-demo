/**
 * UNIT TESTS for EnrollmentService (JavaScript)
 * 
 * This test suite demonstrates:
 * 1. MOCKING - Using Jest to create mock functions
 * 2. STUBBING - Defining return values for mock functions
 * 3. Isolated testing - Testing business logic without real dependencies
 * 
 * Test Doubles Used:
 * - MOCK: Repository methods are mocked using jest.fn()
 * - STUB: We define return values using mockReturnValue() and mockImplementation()
 */

const EnrollmentService = require('../service/EnrollmentService');
const Student = require('../model/Student');
const Course = require('../model/Course');
const Enrollment = require('../model/Enrollment');

describe('EnrollmentService - Unit Tests', () => {
    let enrollmentService;
    let mockStudentRepo;
    let mockCourseRepo;
    let mockEnrollmentRepo;

    beforeEach(() => {
        // Create MOCK repositories with jest.fn()
        mockStudentRepo = {
            findById: jest.fn(),      // MOCK - simulated method
            save: jest.fn()           // MOCK - simulated method
        };

        mockCourseRepo = {
            findById: jest.fn(),      // MOCK - simulated method
            save: jest.fn()           // MOCK - simulated method
        };

        mockEnrollmentRepo = {
            save: jest.fn(),                    // MOCK - simulated method
            findByStudentId: jest.fn(),         // MOCK - simulated method
            findByStudentAndCourse: jest.fn()   // MOCK - simulated method
        };

        // Inject mocked dependencies
        enrollmentService = new EnrollmentService(
            mockStudentRepo,
            mockCourseRepo,
            mockEnrollmentRepo
        );
    });

    // Clear all mocks after each test
    afterEach(() => {
        jest.clearAllMocks();
    });

    // ==================== ENROLLMENT TESTS ====================

    describe('enroll()', () => {
        test('should successfully enroll student when all conditions are met', () => {
            // ARRANGE - Set up test data and STUB behavior
            const studentId = 'S001';
            const courseId = 'C001';
            
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);
            const course = new Course(courseId, 'Java Programming', 3);

            // STUB - Define what mocked methods should return
            mockStudentRepo.findById.mockReturnValue(student);
            mockCourseRepo.findById.mockReturnValue(course);
            mockStudentRepo.save.mockReturnValue(student);
            mockEnrollmentRepo.save.mockImplementation(enrollment => enrollment);

            // ACT - Execute the method under test
            const result = enrollmentService.enroll(studentId, courseId);

            // ASSERT - Verify the results
            expect(result).toBeDefined();
            expect(result.studentId).toBe(studentId);
            expect(result.courseId).toBe(courseId);
            expect(student.currentCredits).toBe(3); // Credits were added

            // Verify mock interactions
            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockStudentRepo.findById).toHaveBeenCalledTimes(1);
            expect(mockCourseRepo.findById).toHaveBeenCalledWith(courseId);
            expect(mockCourseRepo.findById).toHaveBeenCalledTimes(1);
            expect(mockEnrollmentRepo.save).toHaveBeenCalledTimes(1);
            expect(mockStudentRepo.save).toHaveBeenCalledWith(student);
        });

        test('should throw error when student not found (STUB returns null)', () => {
            // ARRANGE
            const studentId = 'S999';
            const courseId = 'C001';

            // STUB - Return null to simulate student not found
            mockStudentRepo.findById.mockReturnValue(null);

            // ACT & ASSERT - Verify exception is thrown
            expect(() => {
                enrollmentService.enroll(studentId, courseId);
            }).toThrow(`Student not found with ID: ${studentId}`);

            // Verify we only tried to find the student (didn't proceed further)
            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockCourseRepo.findById).not.toHaveBeenCalled();
            expect(mockEnrollmentRepo.save).not.toHaveBeenCalled();
        });

        test('should throw error when course not found (STUB returns null)', () => {
            // ARRANGE
            const studentId = 'S001';
            const courseId = 'C999';
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);

            // STUB - Student exists, but course does not
            mockStudentRepo.findById.mockReturnValue(student);
            mockCourseRepo.findById.mockReturnValue(null);

            // ACT & ASSERT
            expect(() => {
                enrollmentService.enroll(studentId, courseId);
            }).toThrow(`Course not found with ID: ${courseId}`);

            // Verify repository interactions
            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockCourseRepo.findById).toHaveBeenCalledWith(courseId);
            expect(mockEnrollmentRepo.save).not.toHaveBeenCalled();
        });

        test('should throw error when credit limit exceeded', () => {
            // ARRANGE
            const studentId = 'S001';
            const courseId = 'C001';

            // Student with 15 credits out of 18 max
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);
            student.addCredits(15); // Already has 15 credits

            // Course with 4 credits (would exceed 18 limit)
            const course = new Course(courseId, 'Advanced Programming', 4);

            // STUB
            mockStudentRepo.findById.mockReturnValue(student);
            mockCourseRepo.findById.mockReturnValue(course);

            // ACT & ASSERT
            expect(() => {
                enrollmentService.enroll(studentId, courseId);
            }).toThrow('Student exceeds maximum credit limit');

            // Verify we checked student and course but didn't save
            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockCourseRepo.findById).toHaveBeenCalledWith(courseId);
            expect(mockEnrollmentRepo.save).not.toHaveBeenCalled();
            expect(mockStudentRepo.save).not.toHaveBeenCalled();
        });
    });

    // ==================== GPA CALCULATION TESTS ====================

    describe('calculateGPA()', () => {
        test('should calculate correct GPA with multiple enrollments', () => {
            // ARRANGE
            const studentId = 'S001';
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);

            // Create courses with different credits
            const course1 = new Course('C001', 'Java Programming', 3);
            const course2 = new Course('C002', 'Data Structures', 4);
            const course3 = new Course('C003', 'Web Development', 3);

            // Create enrollments with grades
            const enrollment1 = new Enrollment(studentId, 'C001');
            enrollment1.grade = 4.0; // A grade

            const enrollment2 = new Enrollment(studentId, 'C002');
            enrollment2.grade = 3.5; // B+ grade

            const enrollment3 = new Enrollment(studentId, 'C003');
            enrollment3.grade = 3.0; // B grade

            // STUB - Define mock behavior
            mockStudentRepo.findById.mockReturnValue(student);
            mockEnrollmentRepo.findByStudentId.mockReturnValue([
                enrollment1,
                enrollment2,
                enrollment3
            ]);
            
            // STUB course lookups
            mockCourseRepo.findById.mockImplementation(courseId => {
                if (courseId === 'C001') return course1;
                if (courseId === 'C002') return course2;
                if (courseId === 'C003') return course3;
                return null;
            });

            // ACT
            const gpa = enrollmentService.calculateGPA(studentId);

            // ASSERT
            // Expected GPA = (4.0*3 + 3.5*4 + 3.0*3) / (3+4+3) = (12 + 14 + 9) / 10 = 3.5
            expect(gpa).toBeCloseTo(3.5, 2);

            // Verify mock interactions
            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockEnrollmentRepo.findByStudentId).toHaveBeenCalledWith(studentId);
            expect(mockCourseRepo.findById).toHaveBeenCalledTimes(3);
        });

        test('should return 0.0 when student has no enrollments', () => {
            // ARRANGE
            const studentId = 'S001';
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);

            // STUB - Student exists but has no enrollments
            mockStudentRepo.findById.mockReturnValue(student);
            mockEnrollmentRepo.findByStudentId.mockReturnValue([]);

            // ACT
            const gpa = enrollmentService.calculateGPA(studentId);

            // ASSERT
            expect(gpa).toBe(0.0);

            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockEnrollmentRepo.findByStudentId).toHaveBeenCalledWith(studentId);
            expect(mockCourseRepo.findById).not.toHaveBeenCalled();
        });

        test('should throw error when student not found', () => {
            // ARRANGE
            const studentId = 'S999';

            // STUB - Return null for non-existent student
            mockStudentRepo.findById.mockReturnValue(null);

            // ACT & ASSERT
            expect(() => {
                enrollmentService.calculateGPA(studentId);
            }).toThrow(`Student not found with ID: ${studentId}`);

            expect(mockStudentRepo.findById).toHaveBeenCalledWith(studentId);
            expect(mockEnrollmentRepo.findByStudentId).not.toHaveBeenCalled();
        });

        test('should handle missing course data gracefully', () => {
            // ARRANGE
            const studentId = 'S001';
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);

            const course1 = new Course('C001', 'Java Programming', 3);

            const enrollment1 = new Enrollment(studentId, 'C001');
            enrollment1.grade = 4.0;

            const enrollment2 = new Enrollment(studentId, 'C999'); // Course doesn't exist
            enrollment2.grade = 3.5;

            // STUB
            mockStudentRepo.findById.mockReturnValue(student);
            mockEnrollmentRepo.findByStudentId.mockReturnValue([enrollment1, enrollment2]);
            mockCourseRepo.findById.mockImplementation(courseId => {
                if (courseId === 'C001') return course1;
                return null; // Missing course
            });

            // ACT
            const gpa = enrollmentService.calculateGPA(studentId);

            // ASSERT
            // Should only calculate GPA for valid course (C001)
            // Expected: (4.0 * 3) / 3 = 4.0
            expect(gpa).toBeCloseTo(4.0, 2);
        });
    });

    // ==================== DEMONSTRATING DIFFERENT MOCK TYPES ====================

    describe('Mock vs Stub demonstrations', () => {
        test('MOCK example: Verify method was called with specific arguments', () => {
            // ARRANGE
            const studentId = 'S001';
            const student = new Student(studentId, 'John Doe', 'john@example.com', 18);
            const course = new Course('C001', 'Java', 3);

            mockStudentRepo.findById.mockReturnValue(student);
            mockCourseRepo.findById.mockReturnValue(course);

            // ACT
            enrollmentService.enroll(studentId, 'C001');

            // MOCK VERIFICATION - Checking HOW the mock was used
            expect(mockStudentRepo.save).toHaveBeenCalledWith(
                expect.objectContaining({
                    id: studentId,
                    currentCredits: 3
                })
            );
        });

        test('STUB example: Controlling return values for different scenarios', () => {
            // ARRANGE - STUB different return values for different calls
            mockStudentRepo.findById
                .mockReturnValueOnce(new Student('S001', 'Alice', 'alice@test.com', 18))
                .mockReturnValueOnce(new Student('S002', 'Bob', 'bob@test.com', 15))
                .mockReturnValueOnce(null); // Third call returns null

            // ACT & ASSERT - Each call gets different stubbed value
            const result1 = mockStudentRepo.findById('S001');
            expect(result1.name).toBe('Alice');

            const result2 = mockStudentRepo.findById('S002');
            expect(result2.name).toBe('Bob');

            const result3 = mockStudentRepo.findById('S999');
            expect(result3).toBeNull();
        });
    });
});
