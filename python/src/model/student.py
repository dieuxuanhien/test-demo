"""
Student Model - represents a student entity
"""

class Student:
    def __init__(self, id: str, name: str, email: str, max_credits: int):
        self.id = id
        self.name = name
        self.email = email
        self.current_credits = 0
        self.max_credits = max_credits
    
    def add_credits(self, credits: int):
        """Add credits when enrolling in a course"""
        self.current_credits += credits
