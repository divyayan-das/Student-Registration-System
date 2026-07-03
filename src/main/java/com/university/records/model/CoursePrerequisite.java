package com.university.records.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CoursePrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course prerequisiteCourse;

    public CoursePrerequisite() {
    }

    public CoursePrerequisite(Course course, Course prerequisiteCourse) {
        this.course = course;
        this.prerequisiteCourse = prerequisiteCourse;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Course getPrerequisiteCourse() {
        return prerequisiteCourse;
    }
}
