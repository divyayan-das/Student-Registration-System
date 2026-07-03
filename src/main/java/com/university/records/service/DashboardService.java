package com.university.records.service;

import com.university.records.model.Course;
import com.university.records.model.Enrollment;
import com.university.records.model.EnrollmentStatus;
import com.university.records.model.Faculty;
import com.university.records.model.Student;
import com.university.records.repository.CourseRepository;
import com.university.records.repository.EnrollmentRepository;
import com.university.records.repository.FacultyRepository;
import com.university.records.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardService(StudentRepository studentRepository,
                            FacultyRepository facultyRepository,
                            CourseRepository courseRepository,
                            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Map<String, Long> adminCounts() {
        Map<String, Long> metrics = new LinkedHashMap<>();
        metrics.put("Students", studentRepository.count());
        metrics.put("Faculty", facultyRepository.count());
        metrics.put("Courses", courseRepository.count());
        metrics.put("Enrollments", enrollmentRepository.count());
        return metrics;
    }

    public List<Student> allStudents() {
        return studentRepository.findAll();
    }

    public List<Faculty> allFaculty() {
        return facultyRepository.findAll();
    }

    public List<Course> allCourses() {
        return courseRepository.findAll();
    }

    public List<Enrollment> allEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Faculty getFaculty(Long id) {
        return facultyRepository.findById(id).orElseThrow();
    }

    public List<Course> facultyCourses(Long facultyId) {
        return courseRepository.findByInstructor(getFaculty(facultyId));
    }

    public List<Enrollment> enrollmentsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return enrollmentRepository.findByCourse(course);
    }

    public double calculateGpa(Student student) {
        List<Enrollment> completed = enrollmentRepository.findByStudent(student).stream()
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .filter(enrollment -> enrollment.getGrade() != null)
                .toList();

        if (completed.isEmpty()) {
            return 0.0;
        }

        double totalPoints = completed.stream()
                .mapToDouble(enrollment -> gradePoints(enrollment.getGrade()) * enrollment.getCourse().getCreditHours())
                .sum();
        int totalCredits = completed.stream()
                .mapToInt(enrollment -> enrollment.getCourse().getCreditHours())
                .sum();
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    private double gradePoints(String grade) {
        return switch (grade.toUpperCase()) {
            case "A+" -> 4.0;
            case "A" -> 4.0;
            case "B+" -> 3.5;
            case "B" -> 3.0;
            case "C+" -> 2.5;
            case "C" -> 2.0;
            case "D" -> 1.0;
            default -> 0.0;
        };
    }
}
