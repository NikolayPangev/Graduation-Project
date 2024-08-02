package org.example.studentmanagementsystem.web;

import jakarta.validation.Valid;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.repository.ClassRepository;
import org.example.studentmanagementsystem.service.ClassService;
import org.example.studentmanagementsystem.service.ParentService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ParentService parentService;

    private final StudentService studentService;

    private final ClassService classService;
    private final ClassRepository classRepository;

    private final SubjectService subjectService;

    public AdminController(ParentService parentService, StudentService studentService, ClassService classService, ClassRepository classRepository, SubjectService subjectService) {
        this.parentService = parentService;
        this.studentService = studentService;
        this.classService = classService;
        this.classRepository = classRepository;
        this.subjectService = subjectService;
    }

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

//    @GetMapping("/createStudent")
//    public String createStudentForm(Model model) {
//        model.addAttribute("studentForm", new StudentForm());
//        model.addAttribute("parents", parentService.getAllParents());
//        model.addAttribute("classes", classService.getAllClasses());
//        return "create_student";
//    }
//
//    @PostMapping("/createStudent")
//    public String createStudent(@ModelAttribute @Valid StudentForm studentForm, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("parents", parentService.getAllParents());
//            model.addAttribute("classes", classService.getAllClasses());
//            return "create_student";
//        }
//        studentService.createStudent(studentForm);
//        return "redirect:/admin/dashboard";
//    }

    @GetMapping("/createClass")
    public String createClassForm(Model model) {
        model.addAttribute("class", new Class());
        return "admin/create_class";
    }

    @PostMapping("/createClass")
    public String createClass(@ModelAttribute @Valid Class newClass, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/create_class";
        }
        try {
            classService.createClass(newClass);
        } catch (IllegalArgumentException e) {
            result.rejectValue("grade", "error.class", e.getMessage());
            model.addAttribute("class", newClass);
            return "admin/create_class";
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/createSubject")
    public String showCreateSubjectForm(Model model) {
        model.addAttribute("errorMessage", null);
        return "admin/create_subject";
    }

    @PostMapping("/createSubject")
    public String createSubject(@RequestParam String subjectName, Model model) {
        if (subjectService.existsByName(subjectName)) {
            model.addAttribute("errorMessage", "This subject already exists");
            return "admin/create_subject";
        }

        subjectService.createSubject(subjectName);
        model.addAttribute("successMessage", "Successfully created a new subject");
        return "admin/create_subject";
    }

    @GetMapping("/deleteSubject")
    public String showDeleteSubjectPage(Model model) {
        model.addAttribute("subjects", subjectService.findAllSubjects());
        return "admin/delete_subject";
    }

    @PostMapping("/deleteSubject")
    public String deleteSubject(@RequestParam Long subjectId) {
        subjectService.deleteSubject(subjectId);
        return "redirect:/admin/deleteSubject";
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
