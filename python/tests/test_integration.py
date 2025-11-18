"""
Integration Tests - Python
Testing full stack from Controller to Repository
"""
import pytest
from src.model.student import Student
from src.model.course import Course
from src.repository.student_repository import StudentRepository
from src.repository.course_repository import CourseRepository
from src.repository.enrollment_repository import EnrollmentRepository
from src.service.enrollment_service import EnrollmentService
from src.controller.enrollment_controller import EnrollmentController


class TestEnrollmentIntegration:
    """Integration tests for complete enrollment flow"""
    
    @pytest.fixture
    def setup(self):
        """Setup all layers for integration testing"""
        # Initialize repositories
        student_repo = StudentRepository()
        course_repo = CourseRepository()
        enrollment_repo = EnrollmentRepository()
        
        # Initialize service
        service = EnrollmentService(student_repo, course_repo, enrollment_repo)
        
        # Initialize controller
        controller = EnrollmentController(service)
        
        # Add test data
        student_repo.save(Student("S001", "John Doe", "john@example.com", 18))
        student_repo.save(Student("S002", "Jane Smith", "jane@example.com", 12))
        
        course_repo.save(Course("C001", "Math 101", 3))
        course_repo.save(Course("C002", "Physics 101", 4))
        course_repo.save(Course("C003", "Chemistry 101", 3))
        
        return {
            'controller': controller,
            'service': service,
            'student_repo': student_repo,
            'course_repo': course_repo,
            'enrollment_repo': enrollment_repo
        }
    
    def test_complete_enrollment_flow_success(self, setup):
        """Integration: Complete enrollment flow - Success"""
        controller = setup['controller']
        enrollment_repo = setup['enrollment_repo']
        student_repo = setup['student_repo']
        
        # Act - Call controller
        result = controller.enroll_student("S001", "C001")
        
        # Assert - Check controller response
        assert "SUCCESS" in result
        assert "S001" in result
        assert "C001" in result
        
        # Assert - Check service created enrollment
        enrollment = enrollment_repo.find_by_student_and_course("S001", "C001")
        assert enrollment is not None
        assert enrollment.student_id == "S001"
        assert enrollment.course_id == "C001"
        
        # Assert - Check repository updated student
        student = student_repo.find_by_id("S001")
        assert student.current_credits == 3
    
    def test_enrollment_flow_invalid_student(self, setup):
        """Integration: Enrollment with invalid student"""
        controller = setup['controller']
        
        result = controller.enroll_student("S999", "C001")
        
        assert "ERROR" in result
        assert "Student not found" in result
    
    def test_multiple_enrollments_and_gpa(self, setup):
        """Integration: Multiple enrollments and GPA calculation"""
        controller = setup['controller']
        enrollment_repo = setup['enrollment_repo']
        student_repo = setup['student_repo']
        
        # Enroll in multiple courses
        result1 = controller.enroll_student("S001", "C001")
        result2 = controller.enroll_student("S001", "C002")
        
        assert "SUCCESS" in result1
        assert "SUCCESS" in result2
        
        # Set grades
        e1 = enrollment_repo.find_by_student_and_course("S001", "C001")
        e2 = enrollment_repo.find_by_student_and_course("S001", "C002")
        e1.set_grade(3.5)
        e2.set_grade(4.0)
        
        # Get GPA through controller
        gpa_result = controller.get_student_gpa("S001")
        
        assert "GPA" in gpa_result
        assert "3.78" in gpa_result or "3.79" in gpa_result
        
        # Verify credits
        student = student_repo.find_by_id("S001")
        assert student.current_credits == 7  # 3 + 4
    
    def test_credit_limit_enforcement(self, setup):
        """Integration: Credit limit enforcement across layers"""
        controller = setup['controller']
        course_repo = setup['course_repo']
        student_repo = setup['student_repo']
        enrollment_repo = setup['enrollment_repo']
        
        # Student S002 has max 12 credits
        controller.enroll_student("S002", "C001")  # 3 credits
        controller.enroll_student("S002", "C002")  # 4 credits (total 7)
        
        # Add course that would exceed limit
        course_repo.save(Course("C004", "Advanced Topics", 6))
        
        # Try to enroll
        result = controller.enroll_student("S002", "C004")
        
        # Should fail
        assert "ERROR" in result
        assert "credit limit" in result
        
        # Verify credits didn't change
        student = student_repo.find_by_id("S002")
        assert student.current_credits == 7
        
        # Verify enrollment wasn't created
        enrollment = enrollment_repo.find_by_student_and_course("S002", "C004")
        assert enrollment is None
    
    def test_student_lifecycle(self, setup):
        """Integration: End-to-end student lifecycle"""
        service = setup['service']
        student_repo = setup['student_repo']
        enrollment_repo = setup['enrollment_repo']
        
        student_id = "S001"
        
        # Enroll in courses
        service.enroll(student_id, "C001")
        assert student_repo.find_by_id(student_id).current_credits == 3
        
        service.enroll(student_id, "C002")
        assert student_repo.find_by_id(student_id).current_credits == 7
        
        # Set grades
        enrollment_repo.find_by_student_and_course(student_id, "C001").set_grade(3.0)
        enrollment_repo.find_by_student_and_course(student_id, "C002").set_grade(4.0)
        
        # Calculate GPA
        gpa = service.calculate_gpa(student_id)
        
        # Verify
        assert abs(gpa - 3.57) < 0.01  # (3*3 + 4*4) / 7 = 3.57
        assert len(enrollment_repo.find_by_student_id(student_id)) == 2
    
    def test_concurrent_enrollments(self, setup):
        """Integration: Different students in same course"""
        controller = setup['controller']
        enrollment_repo = setup['enrollment_repo']
        student_repo = setup['student_repo']
        
        # Enroll different students
        result1 = controller.enroll_student("S001", "C001")
        result2 = controller.enroll_student("S002", "C001")
        
        # Both should succeed
        assert "SUCCESS" in result1
        assert "SUCCESS" in result2
        
        # Verify enrollments
        assert enrollment_repo.find_by_student_and_course("S001", "C001") is not None
        assert enrollment_repo.find_by_student_and_course("S002", "C001") is not None
        
        # Verify credits
        assert student_repo.find_by_id("S001").current_credits == 3
        assert student_repo.find_by_id("S002").current_credits == 3
