"""
Enrollment Repository - manages enrollment data in memory
"""
from typing import List, Optional
from ..model.enrollment import Enrollment


class EnrollmentRepository:
    def __init__(self):
        # In-memory storage using list
        self.enrollments: List[Enrollment] = []
    
    def save(self, enrollment: Enrollment) -> Enrollment:
        self.enrollments.append(enrollment)
        return enrollment
    
    def find_by_student_id(self, student_id: str) -> List[Enrollment]:
        return [e for e in self.enrollments if e.student_id == student_id]
    
    def find_by_student_and_course(self, student_id: str, course_id: str) -> Optional[Enrollment]:
        for enrollment in self.enrollments:
            if enrollment.student_id == student_id and enrollment.course_id == course_id:
                return enrollment
        return None
