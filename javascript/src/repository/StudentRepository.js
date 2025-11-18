/**
 * Student Repository - manages student data in memory
 */
class StudentRepository {
    constructor() {
        // In-memory storage using object
        this.students = {};
    }

    save(student) {
        this.students[student.id] = student;
        return student;
    }

    findById(id) {
        return this.students[id] || null;
    }

    findAll() {
        return Object.values(this.students);
    }
}

module.exports = StudentRepository;
