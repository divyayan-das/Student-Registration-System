package com.university.records.repository;

import com.university.records.model.Course;
import com.university.records.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findBySemesterOffered(Integer semesterOffered);
    List<Course> findByInstructor(Faculty instructor);
}
