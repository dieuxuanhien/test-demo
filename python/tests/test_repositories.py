"""
Unit Tests for Repository Layer - Python
Testing CRUD operations in isolation
"""
import pytest
from src.model.student import Student
from src.model.course import Course
from src.model.enrollment import Enrollment
from src.repository.student_repository import StudentRepository
from src.repository.course_repository import CourseRepository
from src.repository.enrollment_repository import EnrollmentRepository


class TestStudentRepository:
    """Unit tests for StudentRepository"""
    
    @pytest.fixture
    def repository(self):
        return StudentRepository()
    
    def test_save_student(self, repository):
        """Test saving a student"""
        student = Student("S001", "John Doe", "john@example.com", 18)
        saved = repository.save(student)
        
        assert saved is not None
        assert saved.id == "S001"
        assert saved.name == "John Doe"
    
    def test_find_by_id_exists(self, repository):
        """Test finding existing student"""
        student = Student("S001", "John Doe", "john@example.com", 18)
        repository.save(student)
        
        found = repository.find_by_id("S001")
        
        assert found is not None
        assert found.id == "S001"
    
    def test_find_by_id_not_exists(self, repository):
        """Test finding non-existent student"""
        found = repository.find_by_id("S999")
        assert found is None
    
    def test_find_all_empty(self, repository):
        """Test finding all students when empty"""
        students = repository.find_all()
        assert students == []
    
    def test_find_all_multiple(self, repository):
        """Test finding multiple students"""
        repository.save(Student("S001", "John Doe", "john@example.com", 18))
        repository.save(Student("S002", "Jane Smith", "jane@example.com", 18))
        
        students = repository.find_all()
        assert len(students) == 2


class TestCourseRepository:
    """Unit tests for CourseRepository"""
    
    @pytest.fixture
    def repository(self):
        return CourseRepository()
    
    def test_save_course(self, repository):
        """Test saving a course"""
        course = Course("C001", "Math 101", 3)
        saved = repository.save(course)
        
        assert saved is not None
        assert saved.id == "C001"
        assert saved.credits == 3
    
    def test_find_by_id_exists(self, repository):
        """Test finding existing course"""
        course = Course("C001", "Math 101", 3)
        repository.save(course)
        
        found = repository.find_by_id("C001")
        assert found is not None
        assert found.id == "C001"
    
    def test_find_all(self, repository):
        """Test finding all courses"""
        repository.save(Course("C001", "Math 101", 3))
        repository.save(Course("C002", "Physics 101", 4))
        
        courses = repository.find_all()
        assert len(courses) == 2


class TestEnrollmentRepository:
    """Unit tests for EnrollmentRepository"""
    
    @pytest.fixture
    def repository(self):
        return EnrollmentRepository()
    
    def test_save_enrollment(self, repository):
        """Test saving an enrollment"""
        enrollment = Enrollment("S001", "C001")
        saved = repository.save(enrollment)
        
        assert saved is not None
        assert saved.student_id == "S001"
        assert saved.course_id == "C001"
    
    def test_find_by_student_id(self, repository):
        """Test finding enrollments by student"""
        repository.save(Enrollment("S001", "C001"))
        repository.save(Enrollment("S001", "C002"))
        repository.save(Enrollment("S002", "C001"))
        
        enrollments = repository.find_by_student_id("S001")
        assert len(enrollments) == 2
        assert all(e.student_id == "S001" for e in enrollments)
    
    def test_find_by_student_and_course(self, repository):
        """Test finding specific enrollment"""
        repository.save(Enrollment("S001", "C001"))
        
        found = repository.find_by_student_and_course("S001", "C001")
        assert found is not None
        assert found.student_id == "S001"
        assert found.course_id == "C001"
    
    def test_update_grade(self, repository):
        """Test updating enrollment grade"""
        enrollment = Enrollment("S001", "C001")
        repository.save(enrollment)
        
        enrollment.set_grade(3.5)
        
        found = repository.find_by_student_and_course("S001", "C001")
        assert found.grade == 3.5
