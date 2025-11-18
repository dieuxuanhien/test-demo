package com.demo.repository;

import com.demo.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for StudentRepository
 * Testing the Repository layer CRUD operations
 */
class StudentRepositoryTest {
    
    private StudentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new StudentRepository();
    }

    @Test
    @DisplayName("Save student successfully")
    void testSave() {
        // Arrange
        Student student = new Student("S001", "John Doe", "john@example.com", 18);
        
        // Act
        Student saved = repository.save(student);
        
        // Assert
        assertNotNull(saved);
        assertEquals("S001", saved.getId());
        assertEquals("John Doe", saved.getName());
    }

    @Test
    @DisplayName("Find student by ID - exists")
    void testFindById_Exists() {
        // Arrange
        Student student = new Student("S001", "John Doe", "john@example.com", 18);
        repository.save(student);
        
        // Act
        Student found = repository.findById("S001");
        
        // Assert
        assertNotNull(found);
        assertEquals("S001", found.getId());
        assertEquals("John Doe", found.getName());
    }

    @Test
    @DisplayName("Find student by ID - not exists")
    void testFindById_NotExists() {
        // Act
        Student found = repository.findById("S999");
        
        // Assert
        assertNull(found);
    }

    @Test
    @DisplayName("Find all students - empty")
    void testFindAll_Empty() {
        // Act
        List<Student> students = repository.findAll();
        
        // Assert
        assertNotNull(students);
        assertTrue(students.isEmpty());
    }

    @Test
    @DisplayName("Find all students - multiple")
    void testFindAll_Multiple() {
        // Arrange
        repository.save(new Student("S001", "John Doe", "john@example.com", 18));
        repository.save(new Student("S002", "Jane Smith", "jane@example.com", 18));
        
        // Act
        List<Student> students = repository.findAll();
        
        // Assert
        assertNotNull(students);
        assertEquals(2, students.size());
    }

    @Test
    @DisplayName("Update existing student")
    void testUpdate_ExistingStudent() {
        // Arrange
        Student student = new Student("S001", "John Doe", "john@example.com", 18);
        repository.save(student);
        
        // Add credits
        student.addCredits(3);
        
        // Act
        Student updated = repository.save(student);
        Student found = repository.findById("S001");
        
        // Assert
        assertEquals(3, updated.getCurrentCredits());
        assertEquals(3, found.getCurrentCredits());
    }
}
