/**
 * Course Model - represents a course entity
 */
class Course {
    constructor(id, name, credits) {
        this.id = id;
        this.name = name;
        this.credits = credits;
    }
}

module.exports = Course;
