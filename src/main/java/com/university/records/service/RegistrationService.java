package com.university.records.service;

import com.university.records.model.Course;
import com.university.records.model.CoursePrerequisite;
import com.university.records.model.Enrollment;
import com.university.records.model.EnrollmentStatus;
import com.university.records.model.Student;
import com.university.records.repository.CoursePrerequisiteRepository;
import com.university.records.repository.CourseRepository;
import com.university.records.repository.EnrollmentRepository;
import com.university.records.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {

    public static final int MAX_CREDIT_HOURS = 21;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;

    public RegistrationService(StudentRepository studentRepository,
                               CourseRepository courseRepository,
                               EnrollmentRepository enrollmentRepository,
                               CoursePrerequisiteRepository prerequisiteRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    public List<Course> getCoursesForSemester(Integer semester) {
        return courseRepository.findBySemesterOffered(semester);
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudent(getStudent(studentId));
    }

    @Transactional
    public void registerStudent(Long studentId, Long courseId, String termLabel) {
        Student student = getStudent(studentId);
        Course course = getCourse(courseId);

        enrollmentRepository.findByStudentAndCourse(student, course).ifPresent(existing -> {
            throw new AcademicValidationException("Student is already linked to this course.");
        });

        if (!student.getDepartment().getId().equals(course.getDepartment().getId())) {
            throw new AcademicValidationException("Students may only register for courses in their department in this demo.");
        }

        int currentHours = enrollmentRepository.findByStudentAndStatus(student, EnrollmentStatus.ENROLLED)
                .stream()
                .mapToInt(enrollment -> enrollment.getCourse().getCreditHours())
                .sum();
        if (currentHours + course.getCreditHours() > MAX_CREDIT_HOURS) {
            throw new AcademicValidationException("Credit hour limit exceeded. Maximum allowed is " + MAX_CREDIT_HOURS + ".");
        }

        List<CoursePrerequisite> prerequisites = prerequisiteRepository.findByCourse(course);
        for (CoursePrerequisite prerequisite : prerequisites) {
            boolean completed = enrollmentRepository.findByStudentAndCourse(student, prerequisite.getPrerequisiteCourse())
                    .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                    .filter(enrollment -> enrollment.getGrade() != null && !enrollment.getGrade().isBlank())
                    .isPresent();
            if (!completed) {
                throw new AcademicValidationException(
                        "Prerequisite not satisfied: " + prerequisite.getPrerequisiteCourse().getCourseCode());
            }
        }

        enrollmentRepository.save(new Enrollment(student, course, EnrollmentStatus.ENROLLED, termLabel, null));
    }

    @Transactional
    public void submitGrade(Long enrollmentId, String grade) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        enrollment.setGrade(grade);
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
    }
}
