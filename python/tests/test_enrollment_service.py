"""
Unit Tests for EnrollmentService
These tests focus on the Service layer business logic
"""
import pytest
from src.model.student import Student
from src.model.course import Course
from src.repository.student_repository import StudentRepository
from src.repository.course_repository import CourseRepository
from src.repository.enrollment_repository import EnrollmentRepository
from src.service.enrollment_service import EnrollmentService


class TestEnrollmentService:
    
    @pytest.fixture
    def setup(self):
        """Setup test data before each test"""
        # Initialize repositories
        student_repository = StudentRepository()
        course_repository = CourseRepository()
        enrollment_repository = EnrollmentRepository()
        
        # Initialize service
        enrollment_service = EnrollmentService(
            student_repository,
            course_repository,
            enrollment_repository
        )
        
        # Create test students
        student1 = Student("S001", "John Doe", "john@example.com", 18)
        student2 = Student("S002", "Jane Smith", "jane@example.com", 12)
        student_repository.save(student1)
        student_repository.save(student2)
        
        # Create test courses
        course1 = Course("C001", "Math 101", 3)
        course2 = Course("C002", "Physics 101", 4)
        course3 = Course("C003", "Chemistry 101", 3)
        course_repository.save(course1)
        course_repository.save(course2)
        course_repository.save(course3)
        
        return {
            'service': enrollment_service,
            'student_repo': student_repository,
            'course_repo': course_repository,
            'enrollment_repo': enrollment_repository
        }
    
    def test_enrollment_student_not_found(self, setup):
        """
        Test Case 1: Student does not exist
        Expected: Should raise ValueError
        """
        service = setup['service']
        non_existent_student_id = "S999"
        valid_course_id = "C001"
        
        # Act & Assert
        with pytest.raises(ValueError) as exc_info:
            service.enroll(non_existent_student_id, valid_course_id)
        
        assert "Student not found" in str(exc_info.value)
        print(f"✓ Test 1 passed: {exc_info.value}")
    
    def test_enrollment_course_not_found(self, setup):
        """
        Test Case 2: Course does not exist
        Expected: Should raise ValueError
        """
        service = setup['service']
        valid_student_id = "S001"
        non_existent_course_id = "C999"
        
        # Act & Assert
        with pytest.raises(ValueError) as exc_info:
            service.enroll(valid_student_id, non_existent_course_id)
        
        assert "Course not found" in str(exc_info.value)
        print(f"✓ Test 2 passed: {exc_info.value}")
    
    def test_enrollment_exceeds_credit_limit(self, setup):
        """
        Test Case 3: Student exceeds credit limit
        Expected: Should raise RuntimeError
        """
        service = setup['service']
        course_repo = setup['course_repo']
        
        student_id = "S002"  # Has max 12 credits
        course_id1 = "C001"  # 3 credits
        course_id2 = "C002"  # 4 credits
        
        # Enroll in first two courses (3 + 4 = 7 credits)
        service.enroll(student_id, course_id1)
        service.enroll(student_id, course_id2)
        
        # Create a new course that would exceed limit
        large_course = Course("C004", "Advanced Topics", 6)
        course_repo.save(large_course)
        
        # Act & Assert - trying to add 6 more credits (total would be 13 > 12)
        with pytest.raises(RuntimeError) as exc_info:
            service.enroll(student_id, "C004")
        
        assert "exceeds maximum credit limit" in str(exc_info.value)
        print(f"✓ Test 3 passed: {exc_info.value}")
    
    def test_enrollment_success(self, setup):
        """
        Test Case 4: Successful enrollment
        Expected: Enrollment created, student credits updated
        """
        service = setup['service']
        student_repo = setup['student_repo']
        course_repo = setup['course_repo']
        
        student_id = "S001"
        course_id = "C001"
        student = student_repo.find_by_id(student_id)
        course = course_repo.find_by_id(course_id)
        initial_credits = student.current_credits
        
        # Act
        enrollment = service.enroll(student_id, course_id)
        
        # Assert
        assert enrollment is not None
        assert enrollment.student_id == student_id
        assert enrollment.course_id == course_id
        
        # Verify student credits were updated
        updated_student = student_repo.find_by_id(student_id)
        assert updated_student.current_credits == initial_credits + course.credits
        
        print("✓ Test 4 passed: Enrollment successful")
        print(f"  Student credits updated: {initial_credits} → {updated_student.current_credits}")
    
    def test_calculate_gpa(self, setup):
        """
        Bonus Test: Calculate GPA correctly
        """
        service = setup['service']
        
        student_id = "S001"
        
        # Enroll in courses
        e1 = service.enroll(student_id, "C001")  # 3 credits
        e2 = service.enroll(student_id, "C002")  # 4 credits
        
        # Set grades
        e1.set_grade(3.5)  # Math: 3.5 GPA, 3 credits
        e2.set_grade(4.0)  # Physics: 4.0 GPA, 4 credits
        
        # Act
        gpa = service.calculate_gpa(student_id)
        
        # Assert
        # Expected GPA = (3.5*3 + 4.0*4) / (3+4) = (10.5 + 16) / 7 = 26.5 / 7 = 3.785...
        assert abs(gpa - 3.785) < 0.01
        print(f"✓ Bonus test passed: GPA = {gpa:.2f}")
