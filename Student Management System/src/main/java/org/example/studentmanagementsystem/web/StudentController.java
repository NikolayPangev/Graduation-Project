package org.example.studentmanagementsystem.web;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.service.GradeService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.TeacherService;
import org.example.studentmanagementsystem.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    private final TeacherService teacherService;

    private final GradeService gradeService;

    private final UserService userService;

    public StudentController(StudentService studentService, TeacherService teacherService, GradeService gradeService, UserService userService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.gradeService = gradeService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "student/student_dashboard";
    }

    @GetMapping("/view-class")
    public String viewClass(Model model, Principal principal) {
        String username = principal.getName();

        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Long classId = student.getClasses().getClassId();

        List<Student> classmates = studentService.findAllByClassId(classId);

        model.addAttribute("students", classmates);
        return "student/view_class";
    }

    @GetMapping("/view-teachers")
    public String viewTeachers(Model model, Principal principal) {
        String username = principal.getName();

        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Teacher> teachers = teacherService.findByClassId(student.getClassId());

        model.addAttribute("teacherSubjects", teachers);
        return "student/view_teachers";
    }
//
//    @GetMapping("/view-grades")
//    public String viewGrades(Model model, Authentication authentication) {
//        // Fetch the username from the security context
//        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
//
//        // Retrieve the student from the database
//        Student student = studentService.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//
//        // Retrieve the grades for the student
//        List<Grade> grades = gradeService.findByStudentId(student.getId());
//
//        // Calculate average grades for each subject
//        model.addAttribute("grades", grades);
//        return "student/view_grades";
//    }

    @GetMapping("/view-profile")
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        Optional<Student> student = studentService.findByUsername(username);
        if (student.isEmpty()) {
            return "error";
        }
        model.addAttribute("user", student.get());
        return "student/view_profile";
    }


    @GetMapping("/logout-confirmation")
    public String logoutConfirmation() {
        return "student/logout_confirmation";
    }

}
