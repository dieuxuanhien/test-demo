package com.demo.model;

/**
 * Student Model - represents a student entity
 */
public class Student {
    private String id;
    private String name;
    private String email;
    private int currentCredits;
    private int maxCredits;

    public Student(String id, String name, String email, int maxCredits) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.currentCredits = 0;
        this.maxCredits = maxCredits;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getCurrentCredits() {
        return currentCredits;
    }

    public int getMaxCredits() {
        return maxCredits;
    }

    // Add credits when enrolling in a course
    public void addCredits(int credits) {
        this.currentCredits += credits;
    }
}
