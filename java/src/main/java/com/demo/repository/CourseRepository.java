package com.demo.repository;

import com.demo.model.Course;
import java.util.*;

/**
 * Course Repository - manages course data in memory
 */
public class CourseRepository {
    // In-memory storage using HashMap
    private Map<String, Course> courses = new HashMap<>();

    public Course save(Course course) {
        courses.put(course.getId(), course);
        return course;
    }

    public Course findById(String id) {
        return courses.get(id);
    }

    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }
}
