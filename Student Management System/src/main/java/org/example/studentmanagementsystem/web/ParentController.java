package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.mapper.StudentMapper;
import org.example.studentmanagementsystem.model.dtos.SubjectWithGrades;
import org.example.studentmanagementsystem.model.dtos.StudentDTO;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.service.GradeService;
import org.example.studentmanagementsystem.service.ParentService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parent")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final GradeService gradeService;

    public ParentController(ParentService parentService, StudentService studentService, TeacherService teacherService, GradeService gradeService) {
        this.parentService = parentService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.gradeService = gradeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        String username = principal.getName();
        Parent parent = parentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        model.addAttribute("parent", parent);
        return "parent/parent_dashboard";
    }

    @GetMapping("/view-children")
    public String viewChildren(Model model, Principal principal) {
        String username = principal.getName();
        Parent parent = parentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        List<StudentDTO> children = parent.getChildren().stream()
                .map(StudentMapper.INSTANCE::toStudentDTO)
                .collect(Collectors.toList());

        model.addAttribute("children", children);
        return "parent/view_children";
    }

    @GetMapping("/child-dashboard/{childId}")
    public String childDashboard(@PathVariable Long childId, Model model) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        StudentDTO childDTO = StudentMapper.INSTANCE.toStudentDTO(child);
        model.addAttribute("child", childDTO);
        return "parent/child_dashboard";
    }

    @GetMapping("/child-grades/{childId}")
    public String childGrades(@PathVariable Long childId, Model model) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        List<Subject> subjects = child.getClasses().getTeachers().stream()
                .map(Teacher::getSubject)
                .distinct()
                .collect(Collectors.toList());

        List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

        for (Subject subject : subjects) {
            List<Grade> grades = gradeService.findByStudentAndSubject(child, subject);

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
        return "parent/view_grades";
    }

    @GetMapping("/child-teachers/{childId}")
    public String childTeachers(@PathVariable Long childId, Model model) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        Class studentClass = child.getClasses();
        List<Teacher> teachers = studentClass.getTeachers();

        model.addAttribute("teachers", teachers);
        model.addAttribute("child", child);
        return "parent/view_teachers";
    }

    @GetMapping("/view-profile")
    public String viewParentProfile(Model model, Principal principal) {
        String username = principal.getName();
        Parent parent = parentService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        model.addAttribute("user", parent);
        return "parent/view_profile";
    }

    @GetMapping("/logout-confirmation")
    public String parentLogoutConfirmation() {
        return "parent/logout_confirmation";
    }
}
