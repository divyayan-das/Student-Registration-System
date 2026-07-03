package com.university.records.controller;

import com.university.records.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("metrics", dashboardService.adminCounts());
        model.addAttribute("students", dashboardService.allStudents());
        model.addAttribute("facultyMembers", dashboardService.allFaculty());
        model.addAttribute("courses", dashboardService.allCourses());
        model.addAttribute("enrollments", dashboardService.allEnrollments());
        return "admin-dashboard";
    }
}
