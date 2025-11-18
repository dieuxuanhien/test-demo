/**
 * Course Repository - manages course data in memory
 */
class CourseRepository {
    constructor() {
        // In-memory storage using object
        this.courses = {};
    }

    save(course) {
        this.courses[course.id] = course;
        return course;
    }

    findById(id) {
        return this.courses[id] || null;
    }

    findAll() {
        return Object.values(this.courses);
    }
}

module.exports = CourseRepository;
