package com.demo.repository;

import com.demo.model.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for EnrollmentRepository
 */
class EnrollmentRepositoryTest {
    
    private EnrollmentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EnrollmentRepository();
    }

    @Test
    @DisplayName("Save enrollment successfully")
    void testSave() {
        // Arrange
        Enrollment enrollment = new Enrollment("S001", "C001");
        
        // Act
        Enrollment saved = repository.save(enrollment);
        
        // Assert
        assertNotNull(saved);
        assertEquals("S001", saved.getStudentId());
        assertEquals("C001", saved.getCourseId());
    }

    @Test
    @DisplayName("Find enrollments by student ID")
    void testFindByStudentId() {
        // Arrange
        repository.save(new Enrollment("S001", "C001"));
        repository.save(new Enrollment("S001", "C002"));
        repository.save(new Enrollment("S002", "C001"));
        
        // Act
        List<Enrollment> enrollments = repository.findByStudentId("S001");
        
        // Assert
        assertEquals(2, enrollments.size());
        assertTrue(enrollments.stream().allMatch(e -> e.getStudentId().equals("S001")));
    }

    @Test
    @DisplayName("Find enrollment by student and course")
    void testFindByStudentAndCourse_Exists() {
        // Arrange
        repository.save(new Enrollment("S001", "C001"));
        
        // Act
        Enrollment found = repository.findByStudentAndCourse("S001", "C001");
        
        // Assert
        assertNotNull(found);
        assertEquals("S001", found.getStudentId());
        assertEquals("C001", found.getCourseId());
    }

    @Test
    @DisplayName("Find enrollment by student and course - not exists")
    void testFindByStudentAndCourse_NotExists() {
        // Act
        Enrollment found = repository.findByStudentAndCourse("S999", "C999");
        
        // Assert
        assertNull(found);
    }

    @Test
    @DisplayName("Update enrollment grade")
    void testUpdateGrade() {
        // Arrange
        Enrollment enrollment = new Enrollment("S001", "C001");
        repository.save(enrollment);
        
        // Act
        enrollment.setGrade(3.5);
        
        // Assert
        Enrollment found = repository.findByStudentAndCourse("S001", "C001");
        assertEquals(3.5, found.getGrade(), 0.01);
    }
}
