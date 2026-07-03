package com.university.records.config;

import com.university.records.model.AppUser;
import com.university.records.model.Course;
import com.university.records.model.CoursePrerequisite;
import com.university.records.model.Department;
import com.university.records.model.Enrollment;
import com.university.records.model.EnrollmentStatus;
import com.university.records.model.Faculty;
import com.university.records.model.Role;
import com.university.records.model.Student;
import com.university.records.repository.AppUserRepository;
import com.university.records.repository.CoursePrerequisiteRepository;
import com.university.records.repository.CourseRepository;
import com.university.records.repository.DepartmentRepository;
import com.university.records.repository.EnrollmentRepository;
import com.university.records.repository.FacultyRepository;
import com.university.records.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(DepartmentRepository departmentRepository,
                           FacultyRepository facultyRepository,
                           StudentRepository studentRepository,
                           CourseRepository courseRepository,
                           CoursePrerequisiteRepository prerequisiteRepository,
                           EnrollmentRepository enrollmentRepository,
                           AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return;
        }

        Department cse = departmentRepository.save(new Department("Computer Science and Engineering", "CSE"));
        Department eee = departmentRepository.save(new Department("Electrical Engineering", "EEE"));

        Faculty f1 = facultyRepository.save(new Faculty("FAC-100", "Dr. Amrita Sen", "amrita.sen@univ.edu", cse));
        Faculty f2 = facultyRepository.save(new Faculty("FAC-200", "Prof. Nikhil Roy", "nikhil.roy@univ.edu", eee));

        Student s1 = studentRepository.save(new Student("2022-CSE-001", "Anika Sharma", "anika@univ.edu", 4, cse));
        Student s2 = studentRepository.save(new Student("2022-CSE-002", "Rahul Das", "rahul@univ.edu", 4, cse));
        Student s3 = studentRepository.save(new Student("2022-EEE-001", "Meera Iyer", "meera@univ.edu", 4, eee));

        Course c1 = courseRepository.save(new Course("CSE201", "Database Systems", 3, 4, cse, f1));
        Course c2 = courseRepository.save(new Course("CSE202", "Web Engineering", 3, 4, cse, f1));
        Course c3 = courseRepository.save(new Course("CSE101", "Programming Fundamentals", 3, 1, cse, f1));
        Course c4 = courseRepository.save(new Course("EEE210", "Circuit Theory", 3, 4, eee, f2));

        prerequisiteRepository.save(new CoursePrerequisite(c1, c3));
        prerequisiteRepository.save(new CoursePrerequisite(c2, c3));

        enrollmentRepository.save(new Enrollment(s1, c3, EnrollmentStatus.COMPLETED, "Spring 2025", "A"));
        enrollmentRepository.save(new Enrollment(s2, c3, EnrollmentStatus.COMPLETED, "Spring 2025", "B+"));
        enrollmentRepository.save(new Enrollment(s3, c4, EnrollmentStatus.ENROLLED, "Fall 2026", null));
        enrollmentRepository.save(new Enrollment(s1, c1, EnrollmentStatus.ENROLLED, "Fall 2026", null));

        appUserRepository.save(new AppUser("admin", passwordEncoder.encode("admin123"), Role.ADMIN, null));
        appUserRepository.save(new AppUser("faculty1", passwordEncoder.encode("faculty123"), Role.FACULTY, f1.getId()));
        appUserRepository.save(new AppUser("faculty2", passwordEncoder.encode("faculty123"), Role.FACULTY, f2.getId()));
        appUserRepository.save(new AppUser("student1", passwordEncoder.encode("student123"), Role.STUDENT, s1.getId()));
        appUserRepository.save(new AppUser("student2", passwordEncoder.encode("student123"), Role.STUDENT, s2.getId()));
        appUserRepository.save(new AppUser("student3", passwordEncoder.encode("student123"), Role.STUDENT, s3.getId()));
    }
}
