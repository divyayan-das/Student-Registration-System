package com.university.records.controller;

import com.university.records.dto.GradeSubmissionForm;
import com.university.records.model.AppUser;
import com.university.records.service.CurrentUserService;
import com.university.records.service.DashboardService;
import com.university.records.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final DashboardService dashboardService;
    private final RegistrationService registrationService;
    private final CurrentUserService currentUserService;

    public FacultyController(DashboardService dashboardService,
                             RegistrationService registrationService,
                             CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.registrationService = registrationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        AppUser user = currentUserService.getCurrentUser();
        model.addAttribute("courses", dashboardService.facultyCourses(user.getLinkedEntityId()));
        model.addAttribute("gradeForm", new GradeSubmissionForm());
        return "faculty-dashboard";
    }

    @GetMapping("/courses/{courseId}")
    public String courseRoster(@PathVariable Long courseId, Model model) {
        model.addAttribute("enrollments", dashboardService.enrollmentsForCourse(courseId));
        model.addAttribute("courseId", courseId);
        model.addAttribute("gradeForm", new GradeSubmissionForm());
        return "faculty-course";
    }

    @PostMapping("/enrollments/{enrollmentId}/grade")
    public String submitGrade(@PathVariable Long enrollmentId,
                              @Valid @ModelAttribute("gradeForm") GradeSubmissionForm form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Grade cannot be blank.");
            return "redirect:/faculty/dashboard";
        }
        registrationService.submitGrade(enrollmentId, form.getGrade());
        redirectAttributes.addFlashAttribute("success", "Grade submitted successfully.");
        return "redirect:/faculty/dashboard";
    }
}
