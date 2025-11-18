package com.demo.service;

import com.demo.model.*;
import com.demo.repository.*;

import java.util.List;

/**
 * Enrollment Service - contains business logic for enrollment operations
 * This is the layer we will focus on testing!
 */
public class EnrollmentService {
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private EnrollmentRepository enrollmentRepository;

    public EnrollmentService(StudentRepository studentRepository,
                            CourseRepository courseRepository,
                            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Enroll a student in a course
     * Business Rules:
     * 1. Student must exist
     * 2. Course must exist
     * 3. Student must not exceed credit limit
     */
    public Enrollment enroll(String studentId, String courseId) {
        // Rule 1: Check if student exists
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }

        // Rule 2: Check if course exists
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        // Rule 3: Check credit limit
        if (student.getCurrentCredits() + course.getCredits() > student.getMaxCredits()) {
            throw new IllegalStateException("Student exceeds maximum credit limit");
        }

        // Create enrollment
        Enrollment enrollment = new Enrollment(studentId, courseId);
        enrollmentRepository.save(enrollment);

        // Update student credits
        student.addCredits(course.getCredits());
        studentRepository.save(student);

        return enrollment;
    }

    /**
     * Calculate GPA for a student
     * GPA = (Sum of grade * credits) / Total credits
     */
    public double calculateGPA(String studentId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }

        // Get all enrollments for the student
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        if (enrollments.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            Course course = courseRepository.findById(enrollment.getCourseId());
            if (course != null) {
                totalPoints += enrollment.getGrade() * course.getCredits();
                totalCredits += course.getCredits();
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }
}
