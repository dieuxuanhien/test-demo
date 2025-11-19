package com.demo.service;

import com.demo.model.Course;
import com.demo.model.Enrollment;
import com.demo.model.Student;
import com.demo.repository.CourseRepository;
import com.demo.repository.EnrollmentRepository;
import com.demo.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UNIT TEST for EnrollmentService
 * 
 * This test class demonstrates:
 * 1. MOCKING - Using Mockito to create mock objects (simulating repository behavior)
 * 2. STUBBING - Defining return values for specific method calls
 * 3. Isolated testing - Testing business logic without real database dependencies
 * 
 * Test Doubles Used:
 * - MOCK: All three repositories are mocked (studentRepository, courseRepository, enrollmentRepository)
 * - STUB: We stub the return values using when().thenReturn()
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for EnrollmentService")
public class EnrollmentServiceTest {

    @Mock
    private StudentRepository studentRepository;  // MOCK - simulates student data access

    @Mock
    private CourseRepository courseRepository;    // MOCK - simulates course data access

    @Mock
    private EnrollmentRepository enrollmentRepository;  // MOCK - simulates enrollment data access

    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        // Initialize the service with mocked dependencies
        enrollmentService = new EnrollmentService(
            studentRepository, 
            courseRepository, 
            enrollmentRepository
        );
    }

    // ==================== ENROLLMENT TESTS ====================

    @Test
    @DisplayName("enroll() - Should successfully enroll student when all conditions are met")
    void testEnroll_Success() {
        // ARRANGE - Set up test data and stub behavior
        String studentId = "S001";
        String courseId = "C001";
        
        // Create test objects
        Student student = new Student(studentId, "John Doe", "john@example.com", 18);
        Course course = new Course(courseId, "Java Programming", 3);
        
        // STUB - Define what mocked methods should return
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(courseRepository.findById(courseId)).thenReturn(course);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT - Execute the method under test
        Enrollment result = enrollmentService.enroll(studentId, courseId);

        // ASSERT - Verify the results
        assertNotNull(result, "Enrollment should not be null");
        assertEquals(studentId, result.getStudentId(), "Student ID should match");
        assertEquals(courseId, result.getCourseId(), "Course ID should match");
        
        // Verify interactions with mocks
        verify(studentRepository, times(1)).findById(studentId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("enroll() - Should throw exception when student not found (STUB returns null)")
    void testEnroll_StudentNotFound() {
        // ARRANGE
        String studentId = "S999";
        String courseId = "C001";
        
        // STUB - Return null to simulate student not found
        when(studentRepository.findById(studentId)).thenReturn(null);

        // ACT & ASSERT - Verify exception is thrown
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> enrollmentService.enroll(studentId, courseId),
            "Should throw IllegalArgumentException when student not found"
        );
        
        assertEquals("Student not found with ID: " + studentId, exception.getMessage());
        
        // Verify that we only tried to find the student (didn't proceed further)
        verify(studentRepository, times(1)).findById(studentId);
        verify(courseRepository, never()).findById(any());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("enroll() - Should throw exception when course not found (STUB returns null)")
    void testEnroll_CourseNotFound() {
        // ARRANGE
        String studentId = "S001";
        String courseId = "C999";
        Student student = new Student(studentId, "John Doe", "john@example.com", 18);
        
        // STUB - Student exists, but course does not
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(courseRepository.findById(courseId)).thenReturn(null);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> enrollmentService.enroll(studentId, courseId)
        );
        
        assertEquals("Course not found with ID: " + courseId, exception.getMessage());
        
        // Verify repository interactions
        verify(studentRepository, times(1)).findById(studentId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("enroll() - Should throw exception when credit limit exceeded")
    void testEnroll_CreditLimitExceeded() {
        // ARRANGE
        String studentId = "S001";
        String courseId = "C001";
        
        // Student with 15 credits out of 18 max
        Student student = new Student(studentId, "John Doe", "john@example.com", 18);
        student.addCredits(15);  // Already has 15 credits
        
        // Course with 4 credits (would exceed 18 limit)
        Course course = new Course(courseId, "Advanced Programming", 4);
        
        // STUB
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(courseRepository.findById(courseId)).thenReturn(course);

        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> enrollmentService.enroll(studentId, courseId),
            "Should throw when credits would exceed limit"
        );
        
        assertEquals("Student exceeds maximum credit limit", exception.getMessage());
        
        // Verify we checked student and course but didn't save
        verify(studentRepository, times(1)).findById(studentId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(enrollmentRepository, never()).save(any());
        verify(studentRepository, never()).save(any());
    }

    // ==================== GPA CALCULATION TESTS ====================

    @Test
    @DisplayName("calculateGPA() - Should calculate correct GPA with multiple enrollments")
    void testCalculateGPA_MultipleEnrollments() {
        // ARRANGE
        String studentId = "S001";
        Student student = new Student(studentId, "John Doe", "john@example.com", 18);
        
        // Create courses with different credits
        Course course1 = new Course("C001", "Java Programming", 3);
        Course course2 = new Course("C002", "Data Structures", 4);
        Course course3 = new Course("C003", "Web Development", 3);
        
        // Create enrollments with grades
        Enrollment enrollment1 = new Enrollment(studentId, "C001");
        enrollment1.setGrade(4.0);  // A grade
        
        Enrollment enrollment2 = new Enrollment(studentId, "C002");
        enrollment2.setGrade(3.5);  // B+ grade
        
        Enrollment enrollment3 = new Enrollment(studentId, "C003");
        enrollment3.setGrade(3.0);  // B grade
        
        // STUB - Define mock behavior
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(enrollmentRepository.findByStudentId(studentId))
            .thenReturn(Arrays.asList(enrollment1, enrollment2, enrollment3));
        when(courseRepository.findById("C001")).thenReturn(course1);
        when(courseRepository.findById("C002")).thenReturn(course2);
        when(courseRepository.findById("C003")).thenReturn(course3);

        // ACT
        double gpa = enrollmentService.calculateGPA(studentId);

        // ASSERT
        // Expected GPA = (4.0*3 + 3.5*4 + 3.0*3) / (3+4+3) = (12 + 14 + 9) / 10 = 35/10 = 3.5
        assertEquals(3.5, gpa, 0.001, "GPA should be correctly calculated");
        
        // Verify mock interactions
        verify(studentRepository, times(1)).findById(studentId);
        verify(enrollmentRepository, times(1)).findByStudentId(studentId);
        verify(courseRepository, times(3)).findById(any());
    }

    @Test
    @DisplayName("calculateGPA() - Should return 0.0 when student has no enrollments")
    void testCalculateGPA_NoEnrollments() {
        // ARRANGE
        String studentId = "S001";
        Student student = new Student(studentId, "John Doe", "john@example.com", 18);
        
        // STUB - Student exists but has no enrollments
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(enrollmentRepository.findByStudentId(studentId)).thenReturn(Collections.emptyList());

        // ACT
        double gpa = enrollmentService.calculateGPA(studentId);

        // ASSERT
        assertEquals(0.0, gpa, 0.001, "GPA should be 0.0 when no enrollments exist");
        
        verify(studentRepository, times(1)).findById(studentId);
        verify(enrollmentRepository, times(1)).findByStudentId(studentId);
        verify(courseRepository, never()).findById(any());
    }

    @Test
    @DisplayName("calculateGPA() - Should throw exception when student not found")
    void testCalculateGPA_StudentNotFound() {
        // ARRANGE
        String studentId = "S999";
        
        // STUB - Return null for non-existent student
        when(studentRepository.findById(studentId)).thenReturn(null);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> enrollmentService.calculateGPA(studentId)
        );
        
        assertEquals("Student not found with ID: " + studentId, exception.getMessage());
        verify(studentRepository, times(1)).findById(studentId);
        verify(enrollmentRepository, never()).findByStudentId(any());
    }

    @Test
    @DisplayName("calculateGPA() - Should handle missing course data gracefully")
    void testCalculateGPA_MissingCourse() {
        // ARRANGE
        String studentId = "S001";
        Student student = new Student(studentId, "John Doe", "john@example.com", 18);
        
        Course course1 = new Course("C001", "Java Programming", 3);
        
        Enrollment enrollment1 = new Enrollment(studentId, "C001");
        enrollment1.setGrade(4.0);
        
        Enrollment enrollment2 = new Enrollment(studentId, "C999"); // Course doesn't exist
        enrollment2.setGrade(3.5);
        
        // STUB
        when(studentRepository.findById(studentId)).thenReturn(student);
        when(enrollmentRepository.findByStudentId(studentId))
            .thenReturn(Arrays.asList(enrollment1, enrollment2));
        when(courseRepository.findById("C001")).thenReturn(course1);
        when(courseRepository.findById("C999")).thenReturn(null);  // Missing course

        // ACT
        double gpa = enrollmentService.calculateGPA(studentId);

        // ASSERT
        // Should only calculate GPA for valid course (C001)
        // Expected: (4.0 * 3) / 3 = 4.0
        assertEquals(4.0, gpa, 0.001, "Should skip missing course and calculate from valid ones");
    }
}
