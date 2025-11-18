const Enrollment = require('../model/Enrollment');

/**
 * Enrollment Service - contains business logic for enrollment operations
 * This is the layer we will focus on testing!
 */
class EnrollmentService {
    constructor(studentRepository, courseRepository, enrollmentRepository) {
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
    enroll(studentId, courseId) {
        // Rule 1: Check if student exists
        const student = this.studentRepository.findById(studentId);
        if (!student) {
            throw new Error(`Student not found with ID: ${studentId}`);
        }

        // Rule 2: Check if course exists
        const course = this.courseRepository.findById(courseId);
        if (!course) {
            throw new Error(`Course not found with ID: ${courseId}`);
        }

        // Rule 3: Check credit limit
        if (student.currentCredits + course.credits > student.maxCredits) {
            throw new Error('Student exceeds maximum credit limit');
        }

        // Create enrollment
        const enrollment = new Enrollment(studentId, courseId);
        this.enrollmentRepository.save(enrollment);

        // Update student credits
        student.addCredits(course.credits);
        this.studentRepository.save(student);

        return enrollment;
    }

    /**
     * Calculate GPA for a student
     * GPA = (Sum of grade * credits) / Total credits
     */
    calculateGPA(studentId) {
        // Check if student exists
        const student = this.studentRepository.findById(studentId);
        if (!student) {
            throw new Error(`Student not found with ID: ${studentId}`);
        }

        // Get all enrollments for the student
        const enrollments = this.enrollmentRepository.findByStudentId(studentId);
        if (enrollments.length === 0) {
            return 0.0;
        }

        let totalPoints = 0.0;
        let totalCredits = 0;

        enrollments.forEach(enrollment => {
            const course = this.courseRepository.findById(enrollment.courseId);
            if (course) {
                totalPoints += enrollment.grade * course.credits;
                totalCredits += course.credits;
            }
        });

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }
}

module.exports = EnrollmentService;
