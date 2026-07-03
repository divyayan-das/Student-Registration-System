package com.university.records.dto;

import jakarta.validation.constraints.NotBlank;

public class GradeSubmissionForm {

    @NotBlank
    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
