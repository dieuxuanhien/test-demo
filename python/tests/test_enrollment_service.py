"""
UNIT TESTS for EnrollmentService (Python)

This test module demonstrates:
1. MOCKING - Using unittest.mock to create mock objects
2. STUBBING - Defining return values for mock methods
3. Isolated testing - Testing business logic without real dependencies

Test Doubles Used:
- MOCK: Repository objects are mocked using Mock() and MagicMock()
- STUB: We define return values using return_value and side_effect
"""

import unittest
from unittest.mock import Mock, MagicMock, call
import sys
import os

# Add the parent directory to the path so we can import modules
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from src.service.enrollment_service import EnrollmentService
from src.model.student import Student
from src.model.course import Course
from src.model.enrollment import Enrollment


class TestEnrollmentService(unittest.TestCase):
    """Unit tests for EnrollmentService using mocks and stubs"""

    def setUp(self):
        """
        Set up test fixtures before each test
        Creates MOCK repositories instead of real ones
        """
        # Create MOCK repositories - these are test doubles
        self.mock_student_repo = Mock()
        self.mock_course_repo = Mock()
        self.mock_enrollment_repo = Mock()

        # Inject mocked dependencies into the service
        self.enrollment_service = EnrollmentService(
            self.mock_student_repo,
            self.mock_course_repo,
            self.mock_enrollment_repo
        )

    # ==================== ENROLLMENT TESTS ====================

    def test_enroll_success(self):
        """
        Test successful enrollment when all conditions are met
        Demonstrates: STUBBING repository responses for happy path
        """
        # ARRANGE - Set up test data
        student_id = 'S001'
        course_id = 'C001'
        
        student = Student(student_id, 'John Doe', 'john@example.com', 18)
        course = Course(course_id, 'Java Programming', 3)

        # STUB - Define what mocked methods should return
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_course_repo.find_by_id.return_value = course
        self.mock_enrollment_repo.save.return_value = None  # save doesn't return anything

        # ACT - Execute the method under test
        result = self.enrollment_service.enroll(student_id, course_id)

        # ASSERT - Verify the results
        self.assertIsNotNone(result, "Enrollment should not be None")
        self.assertEqual(result.student_id, student_id)
        self.assertEqual(result.course_id, course_id)
        self.assertEqual(student.current_credits, 3, "Student should have 3 credits")

        # Verify interactions with mocks
        self.mock_student_repo.find_by_id.assert_called_once_with(student_id)
        self.mock_course_repo.find_by_id.assert_called_once_with(course_id)
        self.mock_enrollment_repo.save.assert_called_once()
        self.mock_student_repo.save.assert_called_once_with(student)

    def test_enroll_student_not_found(self):
        """
        Test enrollment fails when student doesn't exist
        Demonstrates: STUB returning None to simulate missing data
        """
        # ARRANGE
        student_id = 'S999'
        course_id = 'C001'

        # STUB - Return None to simulate student not found
        self.mock_student_repo.find_by_id.return_value = None

        # ACT & ASSERT - Verify exception is raised
        with self.assertRaises(ValueError) as context:
            self.enrollment_service.enroll(student_id, course_id)

        self.assertIn('Student not found', str(context.exception))
        self.assertIn(student_id, str(context.exception))

        # Verify we only tried to find the student (didn't proceed further)
        self.mock_student_repo.find_by_id.assert_called_once_with(student_id)
        self.mock_course_repo.find_by_id.assert_not_called()
        self.mock_enrollment_repo.save.assert_not_called()

    def test_enroll_course_not_found(self):
        """
        Test enrollment fails when course doesn't exist
        Demonstrates: STUB with conditional returns
        """
        # ARRANGE
        student_id = 'S001'
        course_id = 'C999'
        student = Student(student_id, 'John Doe', 'john@example.com', 18)

        # STUB - Student exists, but course does not
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_course_repo.find_by_id.return_value = None

        # ACT & ASSERT
        with self.assertRaises(ValueError) as context:
            self.enrollment_service.enroll(student_id, course_id)

        self.assertIn('Course not found', str(context.exception))
        self.assertIn(course_id, str(context.exception))

        # Verify repository interactions
        self.mock_student_repo.find_by_id.assert_called_once_with(student_id)
        self.mock_course_repo.find_by_id.assert_called_once_with(course_id)
        self.mock_enrollment_repo.save.assert_not_called()

    def test_enroll_credit_limit_exceeded(self):
        """
        Test enrollment fails when credit limit would be exceeded
        Demonstrates: STUB with pre-configured object state
        """
        # ARRANGE
        student_id = 'S001'
        course_id = 'C001'

        # Student with 15 credits out of 18 max
        student = Student(student_id, 'John Doe', 'john@example.com', 18)
        student.add_credits(15)  # Already has 15 credits

        # Course with 4 credits (would exceed 18 limit)
        course = Course(course_id, 'Advanced Programming', 4)

        # STUB
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_course_repo.find_by_id.return_value = course

        # ACT & ASSERT
        with self.assertRaises(RuntimeError) as context:
            self.enrollment_service.enroll(student_id, course_id)

        self.assertIn('exceeds maximum credit limit', str(context.exception))

        # Verify we checked student and course but didn't save
        self.mock_student_repo.find_by_id.assert_called_once()
        self.mock_course_repo.find_by_id.assert_called_once()
        self.mock_enrollment_repo.save.assert_not_called()
        self.mock_student_repo.save.assert_not_called()

    # ==================== GPA CALCULATION TESTS ====================

    def test_calculate_gpa_multiple_enrollments(self):
        """
        Test GPA calculation with multiple courses
        Demonstrates: STUB with list returns and side_effect for multiple calls
        """
        # ARRANGE
        student_id = 'S001'
        student = Student(student_id, 'John Doe', 'john@example.com', 18)

        # Create courses with different credits
        course1 = Course('C001', 'Java Programming', 3)
        course2 = Course('C002', 'Data Structures', 4)
        course3 = Course('C003', 'Web Development', 3)

        # Create enrollments with grades
        enrollment1 = Enrollment(student_id, 'C001')
        enrollment1.grade = 4.0  # A grade

        enrollment2 = Enrollment(student_id, 'C002')
        enrollment2.grade = 3.5  # B+ grade

        enrollment3 = Enrollment(student_id, 'C003')
        enrollment3.grade = 3.0  # B grade

        # STUB - Define mock behavior
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_enrollment_repo.find_by_student_id.return_value = [
            enrollment1, enrollment2, enrollment3
        ]

        # STUB - Use side_effect to return different courses for different IDs
        def find_course(course_id):
            courses = {'C001': course1, 'C002': course2, 'C003': course3}
            return courses.get(course_id)
        
        self.mock_course_repo.find_by_id.side_effect = find_course

        # ACT
        gpa = self.enrollment_service.calculate_gpa(student_id)

        # ASSERT
        # Expected GPA = (4.0*3 + 3.5*4 + 3.0*3) / (3+4+3) = (12 + 14 + 9) / 10 = 3.5
        self.assertAlmostEqual(gpa, 3.5, places=2)

        # Verify mock interactions
        self.mock_student_repo.find_by_id.assert_called_once_with(student_id)
        self.mock_enrollment_repo.find_by_student_id.assert_called_once_with(student_id)
        self.assertEqual(self.mock_course_repo.find_by_id.call_count, 3)

    def test_calculate_gpa_no_enrollments(self):
        """
        Test GPA returns 0.0 when student has no enrollments
        Demonstrates: STUB returning empty list
        """
        # ARRANGE
        student_id = 'S001'
        student = Student(student_id, 'John Doe', 'john@example.com', 18)

        # STUB - Student exists but has no enrollments
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_enrollment_repo.find_by_student_id.return_value = []

        # ACT
        gpa = self.enrollment_service.calculate_gpa(student_id)

        # ASSERT
        self.assertEqual(gpa, 0.0)

        self.mock_student_repo.find_by_id.assert_called_once_with(student_id)
        self.mock_enrollment_repo.find_by_student_id.assert_called_once_with(student_id)
        self.mock_course_repo.find_by_id.assert_not_called()

    def test_calculate_gpa_student_not_found(self):
        """
        Test GPA calculation fails when student doesn't exist
        Demonstrates: STUB returning None for error condition
        """
        # ARRANGE
        student_id = 'S999'

        # STUB - Return None for non-existent student
        self.mock_student_repo.find_by_id.return_value = None

        # ACT & ASSERT
        with self.assertRaises(ValueError) as context:
            self.enrollment_service.calculate_gpa(student_id)

        self.assertIn('Student not found', str(context.exception))
        self.assertIn(student_id, str(context.exception))

        self.mock_student_repo.find_by_id.assert_called_once_with(student_id)
        self.mock_enrollment_repo.find_by_student_id.assert_not_called()

    def test_calculate_gpa_missing_course(self):
        """
        Test GPA calculation handles missing course data gracefully
        Demonstrates: STUB with partial data (some courses missing)
        """
        # ARRANGE
        student_id = 'S001'
        student = Student(student_id, 'John Doe', 'john@example.com', 18)

        course1 = Course('C001', 'Java Programming', 3)

        enrollment1 = Enrollment(student_id, 'C001')
        enrollment1.grade = 4.0

        enrollment2 = Enrollment(student_id, 'C999')  # Course doesn't exist
        enrollment2.grade = 3.5

        # STUB
        self.mock_student_repo.find_by_id.return_value = student
        self.mock_enrollment_repo.find_by_student_id.return_value = [enrollment1, enrollment2]
        
        # STUB - Return course for C001, None for C999
        def find_course(course_id):
            return course1 if course_id == 'C001' else None
        
        self.mock_course_repo.find_by_id.side_effect = find_course

        # ACT
        gpa = self.enrollment_service.calculate_gpa(student_id)

        # ASSERT
        # Should only calculate GPA for valid course (C001)
        # Expected: (4.0 * 3) / 3 = 4.0
        self.assertAlmostEqual(gpa, 4.0, places=2)

    # ==================== DEMONSTRATING DIFFERENT MOCK TECHNIQUES ====================

    def test_mock_verification_with_specific_arguments(self):
        """
        Demonstrates: Verifying that mocks were called with specific arguments
        This is a MOCK behavior (verifying interactions)
        """
        # ARRANGE
        student_id = 'S001'
        student = Student(student_id, 'John Doe', 'john@example.com', 18)
        course = Course('C001', 'Java', 3)

        self.mock_student_repo.find_by_id.return_value = student
        self.mock_course_repo.find_by_id.return_value = course

        # ACT
        self.enrollment_service.enroll(student_id, 'C001')

        # MOCK VERIFICATION - Checking HOW the mock was used
        self.mock_student_repo.save.assert_called_once()
        # Verify the student object passed has updated credits
        saved_student = self.mock_student_repo.save.call_args[0][0]
        self.assertEqual(saved_student.current_credits, 3)

    def test_stub_with_side_effect_for_exceptions(self):
        """
        Demonstrates: Using side_effect to make stub raise exceptions
        """
        # ARRANGE
        student_id = 'S001'
        
        # STUB - Make the mock raise an exception
        self.mock_student_repo.find_by_id.side_effect = ConnectionError("Database unavailable")

        # ACT & ASSERT
        with self.assertRaises(ConnectionError):
            self.enrollment_service.enroll(student_id, 'C001')


if __name__ == '__main__':
    unittest.main()
