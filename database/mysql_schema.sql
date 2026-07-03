-- University Student Registration and Grade Information System
-- MySQL-oriented database design with procedures, triggers, indexes, and report view.

CREATE DATABASE IF NOT EXISTS university_records;
USE university_records;

CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE faculty (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    faculty_number VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    CONSTRAINT fk_faculty_department
        FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    current_semester INT NOT NULL,
    department_id BIGINT NOT NULL,
    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL,
    credit_hours INT NOT NULL,
    semester_offered INT NOT NULL,
    department_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    CONSTRAINT fk_course_department
        FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_course_instructor
        FOREIGN KEY (instructor_id) REFERENCES faculty(id)
);

CREATE TABLE course_prerequisite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    prerequisite_course_id BIGINT NOT NULL,
    CONSTRAINT fk_prerequisite_course
        FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT fk_prerequisite_required
        FOREIGN KEY (prerequisite_course_id) REFERENCES course(id),
    CONSTRAINT uq_course_prerequisite UNIQUE (course_id, prerequisite_course_id)
);

CREATE TABLE enrollment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    term_label VARCHAR(40) NOT NULL,
    grade VARCHAR(5),
    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_enrollment_course
        FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT uq_student_course UNIQUE (student_id, course_id)
);

CREATE TABLE app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_name VARCHAR(20) NOT NULL,
    linked_entity_id BIGINT NULL
);

CREATE INDEX idx_student_department ON student(department_id);
CREATE INDEX idx_faculty_department ON faculty(department_id);
CREATE INDEX idx_course_department ON course(department_id);
CREATE INDEX idx_course_instructor ON course(instructor_id);
CREATE INDEX idx_enrollment_student_status ON enrollment(student_id, status);
CREATE INDEX idx_enrollment_course_status ON enrollment(course_id, status);

DELIMITER //

CREATE PROCEDURE register_student_for_course(
    IN p_student_id BIGINT,
    IN p_course_id BIGINT,
    IN p_term_label VARCHAR(40)
)
BEGIN
    DECLARE v_credit_hours INT;
    DECLARE v_current_hours INT DEFAULT 0;
    DECLARE v_missing_prereqs INT DEFAULT 0;

    SELECT credit_hours INTO v_credit_hours
    FROM course
    WHERE id = p_course_id;

    SELECT COALESCE(SUM(c.credit_hours), 0)
    INTO v_current_hours
    FROM enrollment e
    JOIN course c ON c.id = e.course_id
    WHERE e.student_id = p_student_id
      AND e.status = 'ENROLLED';

    SELECT COUNT(*)
    INTO v_missing_prereqs
    FROM course_prerequisite cp
    WHERE cp.course_id = p_course_id
      AND NOT EXISTS (
          SELECT 1
          FROM enrollment e
          WHERE e.student_id = p_student_id
            AND e.course_id = cp.prerequisite_course_id
            AND e.status = 'COMPLETED'
            AND e.grade IS NOT NULL
      );

    IF v_missing_prereqs > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Prerequisite requirement not met.';
    END IF;

    IF v_current_hours + v_credit_hours > 21 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Credit-hour limit exceeded.';
    END IF;

    INSERT INTO enrollment(student_id, course_id, status, term_label, grade)
    VALUES (p_student_id, p_course_id, 'ENROLLED', p_term_label, NULL);
END //

CREATE PROCEDURE assign_grade(
    IN p_enrollment_id BIGINT,
    IN p_grade VARCHAR(5)
)
BEGIN
    UPDATE enrollment
    SET grade = p_grade,
        status = 'COMPLETED'
    WHERE id = p_enrollment_id;
END //

CREATE TRIGGER trg_enrollment_status_grade
BEFORE UPDATE ON enrollment
FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' AND (NEW.grade IS NULL OR NEW.grade = '') THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Completed enrollments must contain a grade.';
    END IF;
END //

CREATE TRIGGER trg_prevent_self_prerequisite
BEFORE INSERT ON course_prerequisite
FOR EACH ROW
BEGIN
    IF NEW.course_id = NEW.prerequisite_course_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A course cannot be its own prerequisite.';
    END IF;
END //

DELIMITER ;

CREATE OR REPLACE VIEW student_academic_report AS
SELECT
    s.registration_number,
    s.full_name AS student_name,
    d.code AS department_code,
    c.course_code,
    c.title AS course_title,
    c.credit_hours,
    e.term_label,
    e.status,
    e.grade
FROM enrollment e
JOIN student s ON s.id = e.student_id
JOIN course c ON c.id = e.course_id
JOIN department d ON d.id = s.department_id;

-- ER-style summary
-- department 1---n student
-- department 1---n faculty
-- department 1---n course
-- faculty 1---n course
-- student n---n course via enrollment
-- course n---n prerequisite course via course_prerequisite
