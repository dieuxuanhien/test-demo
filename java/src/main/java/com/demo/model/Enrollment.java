package com.demo.model;

/**
 * Enrollment Model - represents student enrollment in a course
 */
public class Enrollment {
    private String studentId;
    private String courseId;
    private double grade;

    public Enrollment(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = 0.0;
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public double getGrade() {
        return grade;
    }

    // Setter for grade
    public void setGrade(double grade) {
        this.grade = grade;
    }
}
