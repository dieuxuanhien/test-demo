/**
 * Enrollment Model - represents student enrollment in a course
 */
class Enrollment {
    constructor(studentId, courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = 0.0;
    }

    /**
     * Set the grade for this enrollment
     */
    setGrade(grade) {
        this.grade = grade;
    }
}

module.exports = Enrollment;
