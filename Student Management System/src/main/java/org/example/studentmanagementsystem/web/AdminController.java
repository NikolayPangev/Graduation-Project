package org.example.studentmanagementsystem.web;
import org.example.studentmanagementsystem.model.dtos.StudentForm;
import jakarta.validation.Valid;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.repository.ClassRepository;
import org.example.studentmanagementsystem.service.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ParentService parentService;
    private final StudentService studentService;
    private final ClassService classService;
    private final ClassRepository classRepository;
    private final SubjectService subjectService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, ParentService parentService, StudentService studentService, ClassService classService, ClassRepository classRepository, SubjectService subjectService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.parentService = parentService;
        this.studentService = studentService;
        this.classService = classService;
        this.classRepository = classRepository;
        this.subjectService = subjectService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "admin/admin_dashboard";
    }

    @GetMapping("/viewStudents")
    public String viewStudents(Model model) {
        List<Student> students = studentService.findAllStudents();
        List<Class> classes = classService.getAllClassesOrderedByGradeAndSection();
        model.addAttribute("students", students);
        model.addAttribute("classes", classes);
        return "admin/view_students";
    }

    @GetMapping("/createStudent")
    public String createStudentForm(Model model) {
        model.addAttribute("studentForm", new StudentForm());
        return "admin/register_student";
    }

    @PostMapping("/createStudent")
    public String createStudent(@ModelAttribute @Validated StudentForm studentForm,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (userService.usernameExists(studentForm.getUsername())) {
            result.rejectValue("username", "error.username", "Username is already taken");
        }

        if (userService.emailExists(studentForm.getEmail())) {
            result.rejectValue("email", "error.email", "Email is already taken");
        }

        if (!studentForm.getPassword().equals(studentForm.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "admin/register_student";
        }

        userService.createStudent(studentForm);

        redirectAttributes.addFlashAttribute("successMessage", "Student successfully registered!");

        return "redirect:/admin/createStudent";
    }

    @PutMapping("/assignClassToStudent")
    public String assignClassToStudent(@RequestParam Long studentId, @RequestParam Long classId) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Class classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        student.setClasses(classes);
        studentService.save(student);

        return "redirect:/admin/viewStudents";
    }

    @PostMapping("/deleteStudent")
    public String deleteStudent(@RequestParam Long studentId, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(studentId);
            redirectAttributes.addFlashAttribute("successMessage", "Student successfully deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/viewStudents";
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

    @GetMapping("/createClass")
    public String createClassForm(Model model) {
        model.addAttribute("class", new Class());
        model.addAttribute("successMessage", null);
        return "admin/create_class";
    }

    @PostMapping("/createClass")
    public String createClass(@ModelAttribute @Valid Class newClass, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/create_class";
        }
        try {
            classService.createClass(newClass);
            model.addAttribute("successMessage", "The class has been successfully created");
        } catch (IllegalArgumentException e) {
            result.rejectValue("grade", "error.class", e.getMessage());
            model.addAttribute("class", newClass);
            return "admin/create_class";
        }
        model.addAttribute("class", new Class());
        return "admin/create_class";
    }

    @GetMapping("/viewClasses")
    public String viewClasses(Model model) {
        model.addAttribute("classes", classService.getAllClassesOrderedByGradeAndSection());
        return "admin/view_classes";
    }

    @PostMapping("/deleteClass")
    public String deleteClass(@RequestParam Long classId, Model model) {
        try {
            classService.deleteClass(classId);
            model.addAttribute("successMessage", "The class has been successfully deleted");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting class: " + e.getMessage());
        }
        return "redirect:/admin/viewClasses";
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

    @GetMapping("/viewSubjects")
    public String showDeleteSubjectPage(Model model) {
        model.addAttribute("subjects", subjectService.findAllSubjects());
        return "admin/view_subject";
    }

    @PostMapping("/deleteSubject")
    public String deleteSubject(@RequestParam Long subjectId) {
        subjectService.deleteSubject(subjectId);
        return "redirect:/admin/viewSubjects";
    }

    @GetMapping("/createSchedules")
    public String createSchedules(Model model) {
        // Add logic to prepare the schedule creation form
        return "create_schedules"; // The Thymeleaf template for creating schedules
    }

    @GetMapping("/logoutConfirmation")
    public String logoutConfirmation() {
        return "admin/logout_confirmation";
    }
}
