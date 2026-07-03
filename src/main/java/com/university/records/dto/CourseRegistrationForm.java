package com.university.records.dto;

import jakarta.validation.constraints.NotNull;

public class CourseRegistrationForm {

    @NotNull
    private Long courseId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
