package com.university.records.controller;

import com.university.records.dto.CourseRegistrationForm;
import com.university.records.model.AppUser;
import com.university.records.model.Student;
import com.university.records.service.AcademicValidationException;
import com.university.records.service.CurrentUserService;
import com.university.records.service.DashboardService;
import com.university.records.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final RegistrationService registrationService;
    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public StudentController(RegistrationService registrationService,
                             DashboardService dashboardService,
                             CurrentUserService currentUserService) {
        this.registrationService = registrationService;
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AppUser user = currentUserService.getCurrentUser();
        Student student = registrationService.getStudent(user.getLinkedEntityId());
        model.addAttribute("student", student);
        model.addAttribute("availableCourses", registrationService.getCoursesForSemester(student.getCurrentSemester()));
        model.addAttribute("enrollments", registrationService.getStudentEnrollments(student.getId()));
        model.addAttribute("gpa", dashboardService.calculateGpa(student));
        model.addAttribute("registrationForm", new CourseRegistrationForm());
        return "student-dashboard";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") CourseRegistrationForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please choose a course.");
            return "redirect:/student/dashboard";
        }
        try {
            registrationService.registerStudent(user.getLinkedEntityId(), form.getCourseId(), "Fall 2026");
            redirectAttributes.addFlashAttribute("success", "Course registration completed.");
        } catch (AcademicValidationException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}
