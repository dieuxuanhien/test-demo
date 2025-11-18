package com.demo.integration;

import com.demo.controller.EnrollmentController;
import com.demo.model.*;
import com.demo.repository.*;
import com.demo.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests - Testing full stack from Controller to Repository
 * This tests the complete flow through all layers
 */
class EnrollmentIntegrationTest {
    
    private EnrollmentController controller;
    private EnrollmentService service;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
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
    }

    private void setupTestData() {
        // Add students
        studentRepository.save(new Student("S001", "John Doe", "john@example.com", 18));
        studentRepository.save(new Student("S002", "Jane Smith", "jane@example.com", 12));
        
        // Add courses
        courseRepository.save(new Course("C001", "Math 101", 3));
        courseRepository.save(new Course("C002", "Physics 101", 4));
        courseRepository.save(new Course("C003", "Chemistry 101", 3));
    }

    @Test
    @DisplayName("Integration: Complete enrollment flow - Success")
    void testCompleteEnrollmentFlow_Success() {
        // Act - Call controller
        String result = controller.enrollStudent("S001", "C001");
        
        // Assert - Check controller response
        assertTrue(result.contains("SUCCESS"));
        assertTrue(result.contains("S001"));
        assertTrue(result.contains("C001"));
        
        // Assert - Check service layer created enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse("S001", "C001");
        assertNotNull(enrollment);
        assertEquals("S001", enrollment.getStudentId());
        assertEquals("C001", enrollment.getCourseId());
        
        // Assert - Check repository layer updated student
        Student student = studentRepository.findById("S001");
        assertEquals(3, student.getCurrentCredits());
    }

    @Test
    @DisplayName("Integration: Enrollment with invalid student")
    void testEnrollmentFlow_InvalidStudent() {
        // Act
        String result = controller.enrollStudent("S999", "C001");
        
        // Assert
        assertTrue(result.contains("ERROR"));
        assertTrue(result.contains("Student not found"));
    }

    @Test
    @DisplayName("Integration: Multiple enrollments and GPA calculation")
    void testMultipleEnrollmentsAndGPA() {
        // Act - Enroll in multiple courses
        String result1 = controller.enrollStudent("S001", "C001");
        String result2 = controller.enrollStudent("S001", "C002");
        
        // Assert enrollments successful
        assertTrue(result1.contains("SUCCESS"));
        assertTrue(result2.contains("SUCCESS"));
        
        // Set grades
        Enrollment e1 = enrollmentRepository.findByStudentAndCourse("S001", "C001");
        Enrollment e2 = enrollmentRepository.findByStudentAndCourse("S001", "C002");
        e1.setGrade(3.5);
        e2.setGrade(4.0);
        
        // Act - Get GPA through controller
        String gpaResult = controller.getStudentGPA("S001");
        
        // Assert
        assertTrue(gpaResult.contains("GPA"));
        assertTrue(gpaResult.contains("3.78") || gpaResult.contains("3.79"));
        
        // Verify student credits updated correctly
        Student student = studentRepository.findById("S001");
        assertEquals(7, student.getCurrentCredits()); // 3 + 4 = 7
    }

    @Test
    @DisplayName("Integration: Credit limit enforcement across layers")
    void testCreditLimitEnforcement() {
        // Arrange - Student S002 has max 12 credits
        controller.enrollStudent("S002", "C001"); // 3 credits
        controller.enrollStudent("S002", "C002"); // 4 credits (total 7)
        
        // Add a course that would exceed limit
        courseRepository.save(new Course("C004", "Advanced Topics", 6));
        
        // Act - Try to enroll in course that would exceed limit
        String result = controller.enrollStudent("S002", "C004");
        
        // Assert - Should fail
        assertTrue(result.contains("ERROR"));
        assertTrue(result.contains("credit limit"));
        
        // Verify student credits didn't change
        Student student = studentRepository.findById("S002");
        assertEquals(7, student.getCurrentCredits());
        
        // Verify enrollment wasn't created
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse("S002", "C004");
        assertNull(enrollment);
    }

    @Test
    @DisplayName("Integration: End-to-end scenario - Student lifecycle")
    void testStudentLifecycle() {
        String studentId = "S001";
        
        // Step 1: Enroll in first course
        controller.enrollStudent(studentId, "C001");
        Student student = studentRepository.findById(studentId);
        assertEquals(3, student.getCurrentCredits());
        
        // Step 2: Enroll in second course
        controller.enrollStudent(studentId, "C002");
        student = studentRepository.findById(studentId);
        assertEquals(7, student.getCurrentCredits());
        
        // Step 3: Set grades
        enrollmentRepository.findByStudentAndCourse(studentId, "C001").setGrade(3.0);
        enrollmentRepository.findByStudentAndCourse(studentId, "C002").setGrade(4.0);
        
        // Step 4: Calculate GPA
        double gpa = service.calculateGPA(studentId);
        
        // Step 5: Verify final state
        assertEquals(3.57, gpa, 0.01); // (3*3 + 4*4) / 7 = 25/7 = 3.57
        assertEquals(2, enrollmentRepository.findByStudentId(studentId).size());
    }

    @Test
    @DisplayName("Integration: Concurrent enrollments for different students")
    void testConcurrentEnrollments() {
        // Enroll different students in same course
        String result1 = controller.enrollStudent("S001", "C001");
        String result2 = controller.enrollStudent("S002", "C001");
        
        // Both should succeed
        assertTrue(result1.contains("SUCCESS"));
        assertTrue(result2.contains("SUCCESS"));
        
        // Verify both enrollments exist
        assertNotNull(enrollmentRepository.findByStudentAndCourse("S001", "C001"));
        assertNotNull(enrollmentRepository.findByStudentAndCourse("S002", "C001"));
        
        // Verify both students' credits updated
        assertEquals(3, studentRepository.findById("S001").getCurrentCredits());
        assertEquals(3, studentRepository.findById("S002").getCurrentCredits());
    }
}
