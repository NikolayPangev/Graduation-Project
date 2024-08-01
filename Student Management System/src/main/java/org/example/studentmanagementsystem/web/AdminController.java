package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "admin/admin_dashboard";
    }

    @GetMapping("/viewStudents")
    public String viewStudents(Model model) {
        // Add logic to fetch students and add to model
        return "view_students"; // The Thymeleaf template for viewing students
    }

    @GetMapping("/viewTeachers")
    public String viewTeachers(Model model) {
        // Add logic to fetch teachers and add to model
        return "view_teachers"; // The Thymeleaf template for viewing teachers
    }

    @GetMapping("/viewParents")
    public String viewParents(Model model) {
        // Add logic to fetch parents and add to model
        return "view_parents"; // The Thymeleaf template for viewing parents
    }

    @GetMapping("/createUser")
    public String createUserForm(Model model) {
        // Add logic to prepare the form
        return "create_user"; // The Thymeleaf template for creating users
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute User user, Model model) {
        // Add logic to save user and handle redirection
        return "redirect:/admin/dashboard"; // Redirect to the dashboard after creating a user
    }

    @GetMapping("/manageSubjects")
    public String manageSubjects(Model model) {
        // Add logic to prepare the subject management form
        return "manage_subjects"; // The Thymeleaf template for managing subjects
    }

    @GetMapping("/createSchedules")
    public String createSchedules(Model model) {
        // Add logic to prepare the schedule creation form
        return "create_schedules"; // The Thymeleaf template for creating schedules
    }

    @GetMapping("/assignParents")
    public String assignParents(Model model) {
        // Add logic to prepare the parent assignment form
        return "assign_parents"; // The Thymeleaf template for assigning parents
    }
}

