package com.demo.repository;

import com.demo.model.Enrollment;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enrollment Repository - manages enrollment data in memory
 */
public class EnrollmentRepository {
    // In-memory storage using ArrayList
    private List<Enrollment> enrollments = new ArrayList<>();

    public Enrollment save(Enrollment enrollment) {
        enrollments.add(enrollment);
        return enrollment;
    }

    public List<Enrollment> findByStudentId(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public Enrollment findByStudentAndCourse(String studentId, String courseId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
    }
}
