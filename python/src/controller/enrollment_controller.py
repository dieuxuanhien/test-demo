"""
Enrollment Controller - handles requests and calls service layer
This is simplified - in real apps, this would handle HTTP requests
"""
from ..service.enrollment_service import EnrollmentService


class EnrollmentController:
    def __init__(self, enrollment_service: EnrollmentService):
        self.enrollment_service = enrollment_service
    
    def enroll_student(self, student_id: str, course_id: str) -> str:
        """Enroll a student in a course"""
        try:
            enrollment = self.enrollment_service.enroll(student_id, course_id)
            return f"SUCCESS: Student {student_id} enrolled in course {course_id}"
        except Exception as e:
            return f"ERROR: {str(e)}"
    
    def get_student_gpa(self, student_id: str) -> str:
        """Get student GPA"""
        try:
            gpa = self.enrollment_service.calculate_gpa(student_id)
            return f"GPA for student {student_id}: {gpa:.2f}"
        except Exception as e:
            return f"ERROR: {str(e)}"
