package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.SubjectAbsencesDTO;
import org.example.studentmanagementsystem.model.dtos.SubjectWithGrades;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.service.*;
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
    private final AbsenceService absenceService;

    public StudentController(StudentService studentService, TeacherService teacherService, GradeService gradeService, UserService userService, AbsenceService absenceService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.gradeService = gradeService;
        this.userService = userService;
        this.absenceService = absenceService;
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

        List<Subject> subjects = student.getClasses().getTeachers().stream()
                .map(Teacher::getSubject)
                .distinct()
                .collect(Collectors.toList());

        List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

        for (Subject subject : subjects) {
            List<Grade> grades = gradeService.findByStudentAndSubject(student, subject);
            double averageGrade = grades.stream()
                    .mapToDouble(Grade::getGrade)
                    .average()
                    .orElse(0.0);
            Teacher teacher = subject.getTeacher().stream()
                    .findFirst()
                    .orElse(null);
            subjectWithGradesList.add(new SubjectWithGrades(subject, grades, averageGrade, teacher));
        }

        model.addAttribute("subjectWithGradesList", subjectWithGradesList);
        return "student/view_grades";
    }

    @GetMapping("/view-absences")
    public String viewAbsences(Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Subject> subjects = student.getClasses().getTeachers().stream()
                .map(Teacher::getSubject)
                .distinct()
                .collect(Collectors.toList());

        List<SubjectAbsencesDTO> subjectAbsences = subjects.stream().map(subject -> {
            List<Absence> absences = absenceService.findByStudentAndSubject(student, subject);
            return new SubjectAbsencesDTO(subject, absences, absences.size());
        }).collect(Collectors.toList());

        model.addAttribute("subjectAbsences", subjectAbsences);
        return "student/view_absences";
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
