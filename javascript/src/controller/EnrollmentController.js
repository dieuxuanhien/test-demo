/**
 * Enrollment Controller - handles requests and calls service layer
 * This is simplified - in real apps, this would handle HTTP requests
 */
class EnrollmentController {
    constructor(enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Enroll a student in a course
     */
    enrollStudent(studentId, courseId) {
        try {
            const enrollment = this.enrollmentService.enroll(studentId, courseId);
            return `SUCCESS: Student ${studentId} enrolled in course ${courseId}`;
        } catch (error) {
            return `ERROR: ${error.message}`;
        }
    }

    /**
     * Get student GPA
     */
    getStudentGPA(studentId) {
        try {
            const gpa = this.enrollmentService.calculateGPA(studentId);
            return `GPA for student ${studentId}: ${gpa.toFixed(2)}`;
        } catch (error) {
            return `ERROR: ${error.message}`;
        }
    }
}

module.exports = EnrollmentController;
