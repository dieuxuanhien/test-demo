"""
Enrollment Model - represents student enrollment in a course
"""

class Enrollment:
    def __init__(self, student_id: str, course_id: str):
        self.student_id = student_id
        self.course_id = course_id
        self.grade = 0.0
    
    def set_grade(self, grade: float):
        """Set the grade for this enrollment"""
        self.grade = grade
