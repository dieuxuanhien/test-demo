package com.demo.repository;

import com.demo.model.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for CourseRepository
 */
class CourseRepositoryTest {
    
    private CourseRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CourseRepository();
    }

    @Test
    @DisplayName("Save course successfully")
    void testSave() {
        // Arrange
        Course course = new Course("C001", "Math 101", 3);
        
        // Act
        Course saved = repository.save(course);
        
        // Assert
        assertNotNull(saved);
        assertEquals("C001", saved.getId());
        assertEquals("Math 101", saved.getName());
        assertEquals(3, saved.getCredits());
    }

    @Test
    @DisplayName("Find course by ID - exists")
    void testFindById_Exists() {
        // Arrange
        Course course = new Course("C001", "Math 101", 3);
        repository.save(course);
        
        // Act
        Course found = repository.findById("C001");
        
        // Assert
        assertNotNull(found);
        assertEquals("C001", found.getId());
    }

    @Test
    @DisplayName("Find course by ID - not exists")
    void testFindById_NotExists() {
        // Act
        Course found = repository.findById("C999");
        
        // Assert
        assertNull(found);
    }

    @Test
    @DisplayName("Find all courses")
    void testFindAll() {
        // Arrange
        repository.save(new Course("C001", "Math 101", 3));
        repository.save(new Course("C002", "Physics 101", 4));
        
        // Act
        List<Course> courses = repository.findAll();
        
        // Assert
        assertNotNull(courses);
        assertEquals(2, courses.size());
    }
}
