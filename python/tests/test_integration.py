"""
INTEGRATION TESTS for Enrollment System (Python)

This test module demonstrates INTEGRATION TESTING:
- Tests the COMPLETE FLOW from Controller → Service → Repository
- Uses REAL OBJECTS (no mocks/stubs)
- Verifies that all layers work together correctly
- Tests end-to-end business scenarios

Key Differences from Unit Tests:
1. NO MOCKS - All dependencies are real implementations
2. Tests multiple components working together
3. Validates the integration between layers
4. Uses in-memory repositories (simulating real database behavior)

Purpose:
- Ensure components integrate correctly
- Catch interface mismatches
- Verify complete business workflows
"""

import unittest
import sys
import os

# Add the parent directory to the path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from src.controller.enrollment_controller import EnrollmentController
from src.service.enrollment_service import EnrollmentService
from src.repository.student_repository import StudentRepository
from src.repository.course_repository import CourseRepository
from src.repository.enrollment_repository import EnrollmentRepository
from src.model.student import Student
from src.model.course import Course


class TestEnrollmentIntegration(unittest.TestCase):
    """Integration tests for the complete enrollment system"""

    def setUp(self):
        """
        Set up test fixtures before each test
        Creates REAL instances (no mocks!)
        """
        # Create fresh instances for each test - NO MOCKING!
        self.student_repository = StudentRepository()
        self.course_repository = CourseRepository()
        self.enrollment_repository = EnrollmentRepository()

        # Wire up the real dependencies
        self.enrollment_service = EnrollmentService(
            self.student_repository,
            self.course_repository,
            self.enrollment_repository
        )

        self.enrollment_controller = EnrollmentController(self.enrollment_service)

        # Set up test data in repositories
        self._setup_test_data()

    def _setup_test_data(self):
        """
        Initialize repositories with test data
        This simulates what would normally be in a database
        """
        # Add students
        student1 = Student('S001', 'Alice Johnson', 'alice@example.com', 18)
        student2 = Student('S002', 'Bob Smith', 'bob@example.com', 15)
        self.student_repository.save(student1)
        self.student_repository.save(student2)

        # Add courses
        course1 = Course('C001', 'Java Programming', 3)
        course2 = Course('C002', 'Data Structures', 4)
        course3 = Course('C003', 'Web Development', 3)
        course4 = Course('C004', 'Machine Learning', 4)
        self.course_repository.save(course1)
        self.course_repository.save(course2)
        self.course_repository.save(course3)
        self.course_repository.save(course4)

    # ==================== INTEGRATION TEST SCENARIOS ====================

    def test_complete_enrollment_flow_through_all_layers(self):
        """
        SCENARIO: Student successfully enrolls in a course
        FLOW: Controller → Service → Repositories
        VALIDATES: All layers integrate correctly for happy path
        """
        # ACT - Call through the controller (top layer)
        result = self.enrollment_controller.enroll_student('S001', 'C001')

        # ASSERT - Verify response from controller
        self.assertTrue(result.startswith('SUCCESS'))
        self.assertIn('S001', result)
        self.assertIn('C001', result)

        # VERIFY - Check that data was persisted correctly in repository
        saved_enrollment = self.enrollment_repository.find_by_student_and_course('S001', 'C001')
        self.assertIsNotNone(saved_enrollment)
        self.assertEqual(saved_enrollment.student_id, 'S001')
        self.assertEqual(saved_enrollment.course_id, 'C001')

        # VERIFY - Student credits were updated
        student = self.student_repository.find_by_id('S001')
        self.assertEqual(student.current_credits, 3)

    def test_multiple_enrollments_credits_accumulate(self):
        """
        SCENARIO: Student enrolls in multiple courses
        VALIDATES: Credits accumulate correctly across multiple transactions
        """
        # ACT - Enroll in three courses
        self.enrollment_service.enroll('S001', 'C001')  # 3 credits
        self.enrollment_service.enroll('S001', 'C002')  # 4 credits
        self.enrollment_service.enroll('S001', 'C003')  # 3 credits

        # ASSERT - Verify total credits
        student = self.student_repository.find_by_id('S001')
        self.assertEqual(student.current_credits, 10, 
                        "Student should have 10 total credits (3+4+3)")

        # VERIFY - All enrollments were saved
        enrollments = self.enrollment_repository.find_by_student_id('S001')
        self.assertEqual(len(enrollments), 3)

    def test_credit_limit_enforcement(self):
        """
        SCENARIO: Student attempts to exceed credit limit
        VALIDATES: Business rules are enforced across layers
        """
        # ARRANGE - Student S002 has max 15 credits
        self.enrollment_service.enroll('S002', 'C001')  # 3 credits (total: 3)
        self.enrollment_service.enroll('S002', 'C002')  # 4 credits (total: 7)
        self.enrollment_service.enroll('S002', 'C003')  # 3 credits (total: 10)
        self.enrollment_service.enroll('S002', 'C004')  # 4 credits (total: 14)

        # Verify student has 14 credits
        student = self.student_repository.find_by_id('S002')
        self.assertEqual(student.current_credits, 14)

        # Try to add another 4-credit course (would exceed 15)
        new_course = Course('C005', 'Advanced Topics', 4)
        self.course_repository.save(new_course)

        # ACT & ASSERT - Should raise RuntimeError
        with self.assertRaises(RuntimeError) as context:
            self.enrollment_service.enroll('S002', 'C005')

        self.assertIn('exceeds maximum credit limit', str(context.exception))

        # VERIFY - Credits didn't change
        student = self.student_repository.find_by_id('S002')
        self.assertEqual(student.current_credits, 14)

    def test_gpa_calculation_integration_scenario(self):
        """
        SCENARIO: Calculate GPA after multiple enrollments with grades
        VALIDATES: Complete workflow including enrollment, grade assignment, and GPA calculation
        """
        # ARRANGE - Enroll student in courses
        enrollment1 = self.enrollment_service.enroll('S001', 'C001')
        enrollment2 = self.enrollment_service.enroll('S001', 'C002')
        enrollment3 = self.enrollment_service.enroll('S001', 'C003')

        # Set grades (simulate grading after semester)
        enrollment1.grade = 4.0  # A in Java (3 credits)
        enrollment2.grade = 3.5  # B+ in Data Structures (4 credits)
        enrollment3.grade = 3.0  # B in Web Dev (3 credits)

        # ACT - Calculate GPA through service
        gpa = self.enrollment_service.calculate_gpa('S001')

        # ASSERT - Verify GPA calculation
        # Expected: (4.0*3 + 3.5*4 + 3.0*3) / (3+4+3) = (12+14+9)/10 = 3.5
        self.assertAlmostEqual(gpa, 3.5, places=2)

        # VERIFY through controller
        gpa_result = self.enrollment_controller.get_student_gpa('S001')
        self.assertIn('3.50', gpa_result)

    def test_error_handling_student_not_found(self):
        """
        SCENARIO: Attempt to enroll non-existent student
        VALIDATES: Error handling across all layers
        """
        # ACT - Try to enroll non-existent student through controller
        result = self.enrollment_controller.enroll_student('S999', 'C001')

        # ASSERT - Controller should return error message (not raise exception)
        self.assertTrue(result.startswith('ERROR'))
        self.assertIn('Student not found', result)

        # VERIFY - No enrollment was created
        enrollments = self.enrollment_repository.find_by_student_id('S999')
        self.assertEqual(len(enrollments), 0)

    def test_error_handling_course_not_found(self):
        """
        SCENARIO: Attempt to enroll in non-existent course
        VALIDATES: Error handling propagates correctly
        """
        # ACT
        result = self.enrollment_controller.enroll_student('S001', 'C999')

        # ASSERT
        self.assertTrue(result.startswith('ERROR'))
        self.assertIn('Course not found', result)

    def test_multiple_students_same_course(self):
        """
        SCENARIO: Different students enrolling in the same course
        VALIDATES: Repository correctly handles multiple enrollments
        """
        # ACT - Two different students enroll in the same course
        enrollment1 = self.enrollment_service.enroll('S001', 'C001')
        enrollment2 = self.enrollment_service.enroll('S002', 'C001')

        # ASSERT - Both enrollments are independent
        self.assertIsNotNone(enrollment1)
        self.assertIsNotNone(enrollment2)
        self.assertEqual(enrollment1.student_id, 'S001')
        self.assertEqual(enrollment2.student_id, 'S002')
        self.assertEqual(enrollment1.course_id, 'C001')
        self.assertEqual(enrollment2.course_id, 'C001')

        # VERIFY - Each student's credits updated correctly
        student1 = self.student_repository.find_by_id('S001')
        student2 = self.student_repository.find_by_id('S002')
        self.assertEqual(student1.current_credits, 3)
        self.assertEqual(student2.current_credits, 3)

    def test_gpa_no_enrollments(self):
        """
        SCENARIO: Calculate GPA for student with no enrollments
        VALIDATES: Edge case handling across layers
        """
        # ACT - Calculate GPA for student with no enrollments
        gpa = self.enrollment_service.calculate_gpa('S001')

        # ASSERT
        self.assertEqual(gpa, 0.0)

        # VERIFY through controller
        result = self.enrollment_controller.get_student_gpa('S001')
        self.assertIn('0.00', result)

    def test_data_persistence_across_service_calls(self):
        """
        SCENARIO: Verify that data persists across multiple service calls
        VALIDATES: Repository layer correctly maintains state
        """
        # ACT - Perform operations
        self.enrollment_service.enroll('S001', 'C001')

        # VERIFY - Data persists for subsequent calls
        student = self.student_repository.find_by_id('S001')
        self.assertEqual(student.current_credits, 3)

        # ACT - Enroll in another course
        self.enrollment_service.enroll('S001', 'C002')

        # VERIFY - Previous data is still there
        student = self.student_repository.find_by_id('S001')
        self.assertEqual(student.current_credits, 7, "Credits should accumulate (3+4)")

        enrollments = self.enrollment_repository.find_by_student_id('S001')
        self.assertEqual(len(enrollments), 2, "Both enrollments should be persisted")

    def test_enrollment_isolation_between_students(self):
        """
        SCENARIO: Verify that enrollments are properly isolated between students
        VALIDATES: Data integrity in repository layer
        """
        # ACT - Enroll different students in different courses
        self.enrollment_service.enroll('S001', 'C001')
        self.enrollment_service.enroll('S001', 'C002')
        self.enrollment_service.enroll('S002', 'C003')

        # VERIFY - Each student has their own enrollments
        s001_enrollments = self.enrollment_repository.find_by_student_id('S001')
        s002_enrollments = self.enrollment_repository.find_by_student_id('S002')

        self.assertEqual(len(s001_enrollments), 2)
        self.assertEqual(len(s002_enrollments), 1)

        # Verify credits are calculated correctly for each student
        student1 = self.student_repository.find_by_id('S001')
        student2 = self.student_repository.find_by_id('S002')
        self.assertEqual(student1.current_credits, 7)  # 3 + 4
        self.assertEqual(student2.current_credits, 3)

    def test_controller_formats_responses_correctly(self):
        """
        SCENARIO: Verify controller properly formats success and error responses
        VALIDATES: Controller layer's responsibility for response formatting
        """
        # Test success response
        success_result = self.enrollment_controller.enroll_student('S001', 'C001')
        self.assertTrue(success_result.startswith('SUCCESS'))

        # Test error response
        error_result = self.enrollment_controller.enroll_student('S999', 'C001')
        self.assertTrue(error_result.startswith('ERROR'))

        # Test GPA response
        enrollment = self.enrollment_service.enroll('S002', 'C002')
        enrollment.grade = 3.5
        gpa_result = self.enrollment_controller.get_student_gpa('S002')
        self.assertIn('GPA for student', gpa_result)
        self.assertIn('S002', gpa_result)


if __name__ == '__main__':
    unittest.main()
