"""
Student Repository - manages student data in memory
"""
from typing import Dict, List, Optional
from ..model.student import Student


class StudentRepository:
    def __init__(self):
        # In-memory storage using dictionary
        self.students: Dict[str, Student] = {}
    
    def save(self, student: Student) -> Student:
        self.students[student.id] = student
        return student
    
    def find_by_id(self, id: str) -> Optional[Student]:
        return self.students.get(id)
    
    def find_all(self) -> List[Student]:
        return list(self.students.values())
