package com.demo.integration;

import com.demo.controller.EnrollmentController;
import com.demo.model.Course;
import com.demo.model.Enrollment;
import com.demo.model.Student;
import com.demo.repository.CourseRepository;
import com.demo.repository.EnrollmentRepository;
import com.demo.repository.StudentRepository;
import com.demo.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * INTEGRATION TEST for Enrollment System
 * 
 * This test demonstrates INTEGRATION TESTING:
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
 * 
 * Purpose:
 * - Ensure components integrate correctly
 * - Catch interface mismatches
 * - Verify complete business workflows
 */
@DisplayName("Integration Tests for Enrollment System")
public class EnrollmentIntegrationTest {

    // Real instances - NO MOCKS!
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;
    private EnrollmentService enrollmentService;
    private EnrollmentController enrollmentController;

    @BeforeEach
    void setUp() {
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
    }

    /**
     * Initialize repositories with test data
     * This simulates what would normally be in a database
     */
    private void setupTestData() {
        // Add students
        Student student1 = new Student("S001", "Alice Johnson", "alice@example.com", 18);
        Student student2 = new Student("S002", "Bob Smith", "bob@example.com", 15);
        studentRepository.save(student1);
        studentRepository.save(student2);

        // Add courses
        Course course1 = new Course("C001", "Java Programming", 3);
        Course course2 = new Course("C002", "Data Structures", 4);
        Course course3 = new Course("C003", "Web Development", 3);
        Course course4 = new Course("C004", "Machine Learning", 4);
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
    }

    // ==================== INTEGRATION TEST SCENARIOS ====================

    @Test
    @DisplayName("Integration: Complete enrollment flow through all layers")
    void testCompleteEnrollmentFlow() {
        /**
         * SCENARIO: Student successfully enrolls in a course
         * FLOW: Controller → Service → Repositories
         * VALIDATES: All layers integrate correctly for happy path
         */
        
        // ACT - Call through the controller (top layer)
        String result = enrollmentController.enrollStudent("S001", "C001");

        // ASSERT - Verify response from controller
        assertTrue(result.startsWith("SUCCESS"), "Controller should return success message");
        assertTrue(result.contains("S001"), "Response should contain student ID");
        assertTrue(result.contains("C001"), "Response should contain course ID");

        // VERIFY - Check that data was persisted correctly in repository
        Enrollment savedEnrollment = enrollmentRepository.findByStudentAndCourse("S001", "C001");
        assertNotNull(savedEnrollment, "Enrollment should be saved in repository");
        assertEquals("S001", savedEnrollment.getStudentId());
        assertEquals("C001", savedEnrollment.getCourseId());

        // VERIFY - Student credits were updated
        Student student = studentRepository.findById("S001");
        assertEquals(3, student.getCurrentCredits(), "Student should have 3 credits from Java Programming");
    }

    @Test
    @DisplayName("Integration: Multiple enrollments affect student credits correctly")
    void testMultipleEnrollments_CreditsAccumulate() {
        /**
         * SCENARIO: Student enrolls in multiple courses
         * VALIDATES: Credits accumulate correctly across multiple transactions
         */
        
        // ACT - Enroll in three courses
        enrollmentService.enroll("S001", "C001");  // 3 credits
        enrollmentService.enroll("S001", "C002");  // 4 credits
        enrollmentService.enroll("S001", "C003");  // 3 credits

        // ASSERT - Verify total credits
        Student student = studentRepository.findById("S001");
        assertEquals(10, student.getCurrentCredits(), 
            "Student should have 10 total credits (3+4+3)");

        // VERIFY - All enrollments were saved
        var enrollments = enrollmentRepository.findByStudentId("S001");
        assertEquals(3, enrollments.size(), "Should have 3 enrollments");
    }

    @Test
    @DisplayName("Integration: Credit limit enforcement prevents over-enrollment")
    void testCreditLimitEnforcement() {
        /**
         * SCENARIO: Student attempts to exceed credit limit
         * VALIDATES: Business rules are enforced across layers
         */
        
        // ARRANGE - Student S002 has max 15 credits
        enrollmentService.enroll("S002", "C001");  // 3 credits (total: 3)
        enrollmentService.enroll("S002", "C002");  // 4 credits (total: 7)
        enrollmentService.enroll("S002", "C003");  // 3 credits (total: 10)

        // ACT & ASSERT - Trying to add 4 more credits should fail (would be 14, still under 15 is OK)
        // But let's add one more to make it 14
        enrollmentService.enroll("S002", "C004");  // 4 credits (total: 14)
        
        // Now the student has 14 credits out of 15 max
        Student student = studentRepository.findById("S002");
        assertEquals(14, student.getCurrentCredits());

        // Try to add another 4-credit course (would exceed 15)
        Course newCourse = new Course("C005", "Advanced Topics", 4);
        courseRepository.save(newCourse);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> enrollmentService.enroll("S002", "C005"),
            "Should prevent enrollment when it would exceed credit limit"
        );
        
        assertEquals("Student exceeds maximum credit limit", exception.getMessage());
        
        // VERIFY - Credits didn't change
        student = studentRepository.findById("S002");
        assertEquals(14, student.getCurrentCredits(), "Credits should remain at 14");
    }

    @Test
    @DisplayName("Integration: GPA calculation with real enrollment data")
    void testGPACalculation_IntegrationScenario() {
        /**
         * SCENARIO: Calculate GPA after multiple enrollments with grades
         * VALIDATES: Complete workflow including enrollment, grade assignment, and GPA calculation
         */
        
        // ARRANGE - Enroll student in courses
        Enrollment enrollment1 = enrollmentService.enroll("S001", "C001");
        Enrollment enrollment2 = enrollmentService.enroll("S001", "C002");
        Enrollment enrollment3 = enrollmentService.enroll("S001", "C003");

        // Set grades (simulate grading after semester)
        enrollment1.setGrade(4.0);  // A in Java (3 credits)
        enrollment2.setGrade(3.5);  // B+ in Data Structures (4 credits)
        enrollment3.setGrade(3.0);  // B in Web Dev (3 credits)

        // ACT - Calculate GPA through service
        double gpa = enrollmentService.calculateGPA("S001");

        // ASSERT - Verify GPA calculation
        // Expected: (4.0*3 + 3.5*4 + 3.0*3) / (3+4+3) = (12+14+9)/10 = 3.5
        assertEquals(3.5, gpa, 0.001, "GPA should be calculated correctly from real data");

        // VERIFY through controller
        String gpaResult = enrollmentController.getStudentGPA("S001");
        assertTrue(gpaResult.contains("3.50") || gpaResult.contains("3.5"), 
            "Controller should return formatted GPA");
    }

    @Test
    @DisplayName("Integration: Error handling propagates through layers")
    void testErrorHandling_StudentNotFound() {
        /**
         * SCENARIO: Attempt to enroll non-existent student
         * VALIDATES: Error handling across all layers
         */
        
        // ACT - Try to enroll non-existent student through controller
        String result = enrollmentController.enrollStudent("S999", "C001");

        // ASSERT - Controller should return error message (not throw exception)
        assertTrue(result.startsWith("ERROR"), "Controller should return error message");
        assertTrue(result.contains("Student not found"), "Error message should indicate student not found");

        // VERIFY - No enrollment was created
        var enrollments = enrollmentRepository.findByStudentId("S999");
        assertTrue(enrollments.isEmpty(), "No enrollment should be created for non-existent student");
    }

    @Test
    @DisplayName("Integration: Multiple students can enroll in same course")
    void testMultipleStudents_SameCourse() {
        /**
         * SCENARIO: Different students enrolling in the same course
         * VALIDATES: Repository correctly handles multiple enrollments
         */
        
        // ACT - Two different students enroll in the same course
        Enrollment enrollment1 = enrollmentService.enroll("S001", "C001");
        Enrollment enrollment2 = enrollmentService.enroll("S002", "C001");

        // ASSERT - Both enrollments are independent
        assertNotNull(enrollment1);
        assertNotNull(enrollment2);
        assertEquals("S001", enrollment1.getStudentId());
        assertEquals("S002", enrollment2.getStudentId());
        assertEquals("C001", enrollment1.getCourseId());
        assertEquals("C001", enrollment2.getCourseId());

        // VERIFY - Each student's credits updated correctly
        Student student1 = studentRepository.findById("S001");
        Student student2 = studentRepository.findById("S002");
        assertEquals(3, student1.getCurrentCredits());
        assertEquals(3, student2.getCurrentCredits());
    }

    @Test
    @DisplayName("Integration: GPA returns 0.0 for student with no grades")
    void testGPA_NoEnrollments() {
        /**
         * SCENARIO: Calculate GPA for student with no enrollments
         * VALIDATES: Edge case handling across layers
         */
        
        // ACT - Calculate GPA for student with no enrollments
        double gpa = enrollmentService.calculateGPA("S001");

        // ASSERT
        assertEquals(0.0, gpa, 0.001, "GPA should be 0.0 for student with no enrollments");

        // VERIFY through controller
        String result = enrollmentController.getStudentGPA("S001");
        assertTrue(result.contains("0.00") || result.contains("0.0"), 
            "Controller should show 0.00 GPA");
    }

    @Test
    @DisplayName("Integration: Data persistence across service calls")
    void testDataPersistence() {
        /**
         * SCENARIO: Verify that data persists across multiple service calls
         * VALIDATES: Repository layer correctly maintains state
         */
        
        // ACT - Perform operations
        enrollmentService.enroll("S001", "C001");
        
        // VERIFY - Data persists for subsequent calls
        Student student = studentRepository.findById("S001");
        assertEquals(3, student.getCurrentCredits());
        
        // ACT - Enroll in another course
        enrollmentService.enroll("S001", "C002");
        
        // VERIFY - Previous data is still there
        student = studentRepository.findById("S001");
        assertEquals(7, student.getCurrentCredits(), "Credits should accumulate (3+4)");
        
        var enrollments = enrollmentRepository.findByStudentId("S001");
        assertEquals(2, enrollments.size(), "Both enrollments should be persisted");
    }
}
