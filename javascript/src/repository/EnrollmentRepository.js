/**
 * Enrollment Repository - manages enrollment data in memory
 */
class EnrollmentRepository {
    constructor() {
        // In-memory storage using array
        this.enrollments = [];
    }

    save(enrollment) {
        this.enrollments.push(enrollment);
        return enrollment;
    }

    findByStudentId(studentId) {
        return this.enrollments.filter(e => e.studentId === studentId);
    }

    findByStudentAndCourse(studentId, courseId) {
        return this.enrollments.find(
            e => e.studentId === studentId && e.courseId === courseId
        ) || null;
    }
}

module.exports = EnrollmentRepository;
