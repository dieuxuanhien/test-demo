const Student = require('../src/model/Student');
const Course = require('../src/model/Course');
const Enrollment = require('../src/model/Enrollment');
const StudentRepository = require('../src/repository/StudentRepository');
const CourseRepository = require('../src/repository/CourseRepository');
const EnrollmentRepository = require('../src/repository/EnrollmentRepository');

/**
 * Unit Tests for Repository Layer - JavaScript
 * Testing CRUD operations in isolation
 */

describe('StudentRepository', () => {
    let repository;

    beforeEach(() => {
        repository = new StudentRepository();
    });

    test('Save student successfully', () => {
        const student = new Student('S001', 'John Doe', 'john@example.com', 18);
        const saved = repository.save(student);

        expect(saved).toBeDefined();
        expect(saved.id).toBe('S001');
        expect(saved.name).toBe('John Doe');
    });

    test('Find student by ID - exists', () => {
        const student = new Student('S001', 'John Doe', 'john@example.com', 18);
        repository.save(student);

        const found = repository.findById('S001');

        expect(found).toBeDefined();
        expect(found.id).toBe('S001');
    });

    test('Find student by ID - not exists', () => {
        const found = repository.findById('S999');
        expect(found).toBeNull();
    });

    test('Find all students - empty', () => {
        const students = repository.findAll();
        expect(students).toEqual([]);
    });

    test('Find all students - multiple', () => {
        repository.save(new Student('S001', 'John Doe', 'john@example.com', 18));
        repository.save(new Student('S002', 'Jane Smith', 'jane@example.com', 18));

        const students = repository.findAll();
        expect(students).toHaveLength(2);
    });

    test('Update existing student credits', () => {
        const student = new Student('S001', 'John Doe', 'john@example.com', 18);
        repository.save(student);

        student.addCredits(3);
        repository.save(student);

        const found = repository.findById('S001');
        expect(found.currentCredits).toBe(3);
    });
});

describe('CourseRepository', () => {
    let repository;

    beforeEach(() => {
        repository = new CourseRepository();
    });

    test('Save course successfully', () => {
        const course = new Course('C001', 'Math 101', 3);
        const saved = repository.save(course);

        expect(saved).toBeDefined();
        expect(saved.id).toBe('C001');
        expect(saved.name).toBe('Math 101');
        expect(saved.credits).toBe(3);
    });

    test('Find course by ID', () => {
        const course = new Course('C001', 'Math 101', 3);
        repository.save(course);

        const found = repository.findById('C001');
        expect(found).toBeDefined();
        expect(found.id).toBe('C001');
    });

    test('Find all courses', () => {
        repository.save(new Course('C001', 'Math 101', 3));
        repository.save(new Course('C002', 'Physics 101', 4));

        const courses = repository.findAll();
        expect(courses).toHaveLength(2);
    });
});

describe('EnrollmentRepository', () => {
    let repository;

    beforeEach(() => {
        repository = new EnrollmentRepository();
    });

    test('Save enrollment successfully', () => {
        const enrollment = new Enrollment('S001', 'C001');
        const saved = repository.save(enrollment);

        expect(saved).toBeDefined();
        expect(saved.studentId).toBe('S001');
        expect(saved.courseId).toBe('C001');
    });

    test('Find enrollments by student ID', () => {
        repository.save(new Enrollment('S001', 'C001'));
        repository.save(new Enrollment('S001', 'C002'));
        repository.save(new Enrollment('S002', 'C001'));

        const enrollments = repository.findByStudentId('S001');
        expect(enrollments).toHaveLength(2);
        expect(enrollments.every(e => e.studentId === 'S001')).toBe(true);
    });

    test('Find enrollment by student and course', () => {
        repository.save(new Enrollment('S001', 'C001'));

        const found = repository.findByStudentAndCourse('S001', 'C001');
        expect(found).toBeDefined();
        expect(found.studentId).toBe('S001');
        expect(found.courseId).toBe('C001');
    });

    test('Update enrollment grade', () => {
        const enrollment = new Enrollment('S001', 'C001');
        repository.save(enrollment);

        enrollment.setGrade(3.5);

        const found = repository.findByStudentAndCourse('S001', 'C001');
        expect(found.grade).toBe(3.5);
    });
});
