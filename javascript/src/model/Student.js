/**
 * Student Model - represents a student entity
 */
class Student {
    constructor(id, name, email, maxCredits) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.currentCredits = 0;
        this.maxCredits = maxCredits;
    }

    /**
     * Add credits when enrolling in a course
     */
    addCredits(credits) {
        this.currentCredits += credits;
    }
}

module.exports = Student;
