"""
Course Repository - manages course data in memory
"""
from typing import Dict, List, Optional
from ..model.course import Course


class CourseRepository:
    def __init__(self):
        # In-memory storage using dictionary
        self.courses: Dict[str, Course] = {}
    
    def save(self, course: Course) -> Course:
        self.courses[course.id] = course
        return course
    
    def find_by_id(self, id: str) -> Optional[Course]:
        return self.courses.get(id)
    
    def find_all(self) -> List[Course]:
        return list(self.courses.values())
