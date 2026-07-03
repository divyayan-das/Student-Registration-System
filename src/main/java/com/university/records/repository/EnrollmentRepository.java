package com.university.records.repository;

import com.university.records.model.Course;
import com.university.records.model.Enrollment;
import com.university.records.model.EnrollmentStatus;
import com.university.records.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByStudentAndStatus(Student student, EnrollmentStatus status);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
    List<Enrollment> findByCourse(Course course);
}
