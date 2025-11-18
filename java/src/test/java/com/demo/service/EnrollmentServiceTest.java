
package com.demo.service;

import com.demo.model.*;
import com.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for EnrollmentService
 * These tests focus on the Service layer business logic
 */
class EnrollmentServiceTest {
    
    private EnrollmentService enrollmentService;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
        // Initialize repositories
        studentRepository = new StudentRepository();
        courseRepository = new CourseRepository();
        enrollmentRepository = new EnrollmentRepository();
        
        // Initialize service with repositories
        enrollmentService = new EnrollmentService(
            studentRepository, 
            courseRepository, 
            enrollmentRepository
        );
        
        // Setup test data
        setupTestData();
    }

    private void setupTestData() {
        // Create test students
        Student student1 = new Student("S001", "John Doe", "john@example.com", 18);
        Student student2 = new Student("S002", "Jane Smith", "jane@example.com", 12);
        studentRepository.save(student1);
        studentRepository.save(student2);
        
        // Create test courses
        Course course1 = new Course("C001", "Math 101", 3);
        Course course2 = new Course("C002", "Physics 101", 4);
        Course course3 = new Course("C003", "Chemistry 101", 3);
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
    }

    /**
     * Test Case 1: Student does not exist
     * Expected: Should throw IllegalArgumentException
     */
    @Test
    @DisplayName("Test 1: Enrollment fails when student does not exist")
    void testEnrollment_StudentNotFound() {
        // Arrange
        String nonExistentStudentId = "S999";
        String validCourseId = "C001";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> enrollmentService.enroll(nonExistentStudentId, validCourseId)
        );
        
        assertTrue(exception.getMessage().contains("Student not found"));
        System.out.println("✓ Test 1 passed: " + exception.getMessage());
    }

    /**
     * Test Case 2: Course does not exist
     * Expected: Should throw IllegalArgumentException
     */
    @Test
    @DisplayName("Test 2: Enrollment fails when course does not exist")
    void testEnrollment_CourseNotFound() {
        // Arrange
        String validStudentId = "S001";
        String nonExistentCourseId = "C999";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> enrollmentService.enroll(validStudentId, nonExistentCourseId)
        );
        
        assertTrue(exception.getMessage().contains("Course not found"));
        System.out.println("✓ Test 2 passed: " + exception.getMessage());
    }

    /**
     * Test Case 3: Student exceeds credit limit
     * Expected: Should throw IllegalStateException
     */
    @Test
    @DisplayName("Test 3: Enrollment fails when student exceeds credit limit")
    void testEnrollment_ExceedsCreditLimit() {
        // Arrange
        String studentId = "S002"; // Has max 12 credits
        String courseId1 = "C001"; // 3 credits
        String courseId2 = "C002"; // 4 credits
        String courseId3 = "C003"; // 3 credits (would exceed 12)
        
        // Enroll in first two courses (3 + 4 = 7 credits)
        enrollmentService.enroll(studentId, courseId1);
        enrollmentService.enroll(studentId, courseId2);
        
        // Create a new course that would exceed limit
        Course largeCourse = new Course("C004", "Advanced Topics", 6);
        courseRepository.save(largeCourse);
        
        // Act & Assert - trying to add 6 more credits (total would be 13 > 12)
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> enrollmentService.enroll(studentId, "C004")
        );
        
        assertTrue(exception.getMessage().contains("exceeds maximum credit limit"));
        System.out.println("✓ Test 3 passed: " + exception.getMessage());
    }

    /**
     * Test Case 4: Successful enrollment
     * Expected: Enrollment created, student credits updated
     */
    @Test
    @DisplayName("Test 4: Successful enrollment when all conditions are met")
    void testEnrollment_Success() {
        // Arrange
        String studentId = "S001";
        String courseId = "C001";
        Student student = studentRepository.findById(studentId);
        Course course = courseRepository.findById(courseId);
        int initialCredits = student.getCurrentCredits();
        
        // Act
        Enrollment enrollment = enrollmentService.enroll(studentId, courseId);
        
        // Assert
        assertNotNull(enrollment);
        assertEquals(studentId, enrollment.getStudentId());
        assertEquals(courseId, enrollment.getCourseId());
        
        // Verify student credits were updated
        Student updatedStudent = studentRepository.findById(studentId);
        assertEquals(initialCredits + course.getCredits(), updatedStudent.getCurrentCredits());
        
        System.out.println("✓ Test 4 passed: Enrollment successful");
        System.out.println("  Student credits updated: " + initialCredits + " → " + updatedStudent.getCurrentCredits());
    }

    /**
     * Bonus Test: Calculate GPA
     */
    @Test
    @DisplayName("Bonus: Calculate GPA correctly")
    void testCalculateGPA() {
        // Arrange
        String studentId = "S001";
        
        // Enroll in courses
        Enrollment e1 = enrollmentService.enroll(studentId, "C001"); // 3 credits
        Enrollment e2 = enrollmentService.enroll(studentId, "C002"); // 4 credits
        
        // Set grades
        e1.setGrade(3.5); // Math: 3.5 GPA, 3 credits
        e2.setGrade(4.0); // Physics: 4.0 GPA, 4 credits
        
        // Act
        double gpa = enrollmentService.calculateGPA(studentId);
        
        // Assert
        // Expected GPA = (3.5*3 + 4.0*4) / (3+4) = (10.5 + 16) / 7 = 26.5 / 7 = 3.785...
        assertEquals(3.785, gpa, 0.01);
        System.out.println("✓ Bonus test passed: GPA = " + String.format("%.2f", gpa));
    }
}
