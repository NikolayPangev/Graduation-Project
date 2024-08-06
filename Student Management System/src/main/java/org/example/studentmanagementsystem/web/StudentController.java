package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.SubjectWithGrades;
import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.service.GradeService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.TeacherService;
import org.example.studentmanagementsystem.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

        List<Teacher> teachers = teacherService.findByClassId(student.getClasses().getClassId());

        model.addAttribute("teachers", teachers);
        return "student/view_teachers";
    }

    @GetMapping("/view-grades")
    public String viewGrades(Model model, Principal principal) {
        String username = principal.getName();

        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get the list of subjects associated with the student's class
        List<Subject> subjects = student.getClasses().getTeachers().stream()
                .map(Teacher::getSubject)
                .distinct()
                .collect(Collectors.toList());

        List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

        for (Subject subject : subjects) {
            // Fetch grades for the student and subject
            List<Grade> grades = gradeService.findByStudentAndSubject(student, subject);

            // Calculate the average grade
            double averageGrade = grades.stream()
                    .mapToDouble(Grade::getGrade)
                    .average()
                    .orElse(0.0);

            // Fetch the first teacher associated with the subject (assuming there's at least one)
            Teacher teacher = subject.getTeacher().stream()
                    .findFirst()
                    .orElse(null);

            // Create a SubjectWithGrades DTO and add it to the list
            subjectWithGradesList.add(new SubjectWithGrades(subject, grades, averageGrade, teacher));
        }

        // Add the list to the model
        model.addAttribute("subjectWithGradesList", subjectWithGradesList);
        return "student/view_grades";
    }


    private double calculateAverageGrade(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }
        return grades.stream().mapToDouble(Grade::getGrade).average().orElse(0.0);
    }

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
