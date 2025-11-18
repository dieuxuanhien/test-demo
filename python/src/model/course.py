"""
Course Model - represents a course entity
"""

class Course:
    def __init__(self, id: str, name: str, credits: int):
        self.id = id
        self.name = name
        self.credits = credits
