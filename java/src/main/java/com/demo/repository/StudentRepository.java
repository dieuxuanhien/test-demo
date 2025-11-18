package com.demo.repository;

import com.demo.model.Student;
import java.util.*;

/**
 * Student Repository - manages student data in memory
 */
public class StudentRepository {
    // In-memory storage using HashMap
    private Map<String, Student> students = new HashMap<>();

    public Student save(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    public Student findById(String id) {
        return students.get(id);
    }

    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }
}
