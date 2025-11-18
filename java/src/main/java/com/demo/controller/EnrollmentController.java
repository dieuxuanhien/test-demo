package com.demo.controller;

import com.demo.model.Enrollment;
import com.demo.service.EnrollmentService;

/**
 * Enrollment Controller - handles requests and calls service layer
 * This is simplified - in real apps, this would handle HTTP requests
 */
public class EnrollmentController {
    private EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Enroll a student in a course
     */
    public String enrollStudent(String studentId, String courseId) {
        try {
            Enrollment enrollment = enrollmentService.enroll(studentId, courseId);
            return "SUCCESS: Student " + studentId + " enrolled in course " + courseId;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Get student GPA
     */
    public String getStudentGPA(String studentId) {
        try {
            double gpa = enrollmentService.calculateGPA(studentId);
            return "GPA for student " + studentId + ": " + String.format("%.2f", gpa);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
