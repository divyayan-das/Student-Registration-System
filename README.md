# University Academic Records System

A full-stack web application for managing student course registration and grade information at a university. Built as the Database Systems Lab (CSS 2212) mini project at MIT Manipal.

## Features

- **Role-based access control** : separate secured dashboards for Administrator, Faculty, and Student roles (Spring Security)
- **Validated course registration** : enforces prerequisite completion, semester credit-hour limits, and duplicate-enrollment checks before a student can register
- **Grade management** : faculty can view course rosters and submit grades; submission transitions enrollment status transactionally
- **Academic reporting** : students see enrollment and grade reports; admins see aggregate views of students, faculty, courses, and enrollments

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring MVC |
| Security | Spring Security (role-based access) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL schema (normalized, 7 tables) with stored procedures, triggers & indexes; H2 in MySQL-compatibility mode for the runnable demo |
| Frontend | Thymeleaf, CSS |
| Build | Maven |

## Database Design

Normalized relational model: `department`, `faculty`, `student`, `course`, `course_prerequisite`, `enrollment`, `app_user`. The full DDL — including `register_student_for_course` and `assign_grade` procedures, integrity triggers, and the indexing strategy - is in [`database/mysql_schema.sql`](database/mysql_schema.sql).

## Run Locally

```bash
mvn spring-boot:run
```

Open `http://localhost:8081`. Demo accounts are listed on the login page (admin / faculty / student roles).

## Authors

Divyayan Das · Rajveer Singh Sidhu
