"""
Enrollment Service - contains business logic for enrollment operations
This is the layer we will focus on testing!
"""
from ..model.enrollment import Enrollment
from ..repository.student_repository import StudentRepository
from ..repository.course_repository import CourseRepository
from ..repository.enrollment_repository import EnrollmentRepository


class EnrollmentService:
    def __init__(self, 
                 student_repository: StudentRepository,
                 course_repository: CourseRepository,
                 enrollment_repository: EnrollmentRepository):
        self.student_repository = student_repository
        self.course_repository = course_repository
        self.enrollment_repository = enrollment_repository
    
    def enroll(self, student_id: str, course_id: str) -> Enrollment:
        """
        Enroll a student in a course
        Business Rules:
        1. Student must exist
        2. Course must exist
        3. Student must not exceed credit limit
        """
        # Rule 1: Check if student exists
        student = self.student_repository.find_by_id(student_id)
        if student is None:
            raise ValueError(f"Student not found with ID: {student_id}")
        
        # Rule 2: Check if course exists
        course = self.course_repository.find_by_id(course_id)
        if course is None:
            raise ValueError(f"Course not found with ID: {course_id}")
        
        # Rule 3: Check credit limit
        if student.current_credits + course.credits > student.max_credits:
            raise RuntimeError("Student exceeds maximum credit limit")
        
        # Create enrollment
        enrollment = Enrollment(student_id, course_id)
        self.enrollment_repository.save(enrollment)
        
        # Update student credits
        student.add_credits(course.credits)
        self.student_repository.save(student)
        
        return enrollment
    
    def calculate_gpa(self, student_id: str) -> float:
        """
        Calculate GPA for a student
        GPA = (Sum of grade * credits) / Total credits
        """
        # Check if student exists
        student = self.student_repository.find_by_id(student_id)
        if student is None:
            raise ValueError(f"Student not found with ID: {student_id}")
        
        # Get all enrollments for the student
        enrollments = self.enrollment_repository.find_by_student_id(student_id)
        if not enrollments:
            return 0.0
        
        total_points = 0.0
        total_credits = 0
        
        for enrollment in enrollments:
            course = self.course_repository.find_by_id(enrollment.course_id)
            if course is not None:
                total_points += enrollment.grade * course.credits
                total_credits += course.credits
        
        return total_points / total_credits if total_credits > 0 else 0.0
