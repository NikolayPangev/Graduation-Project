package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.service.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    private final ClassService classService;

    private final StudentService studentService;

    private final ParentService parentService;

    private final GradeService gradeService;

    private final SubjectService subjectService;

    private final UserService userService;

    public TeacherController(TeacherService teacherService, ClassService classService, StudentService studentService, ParentService parentService, GradeService gradeService, SubjectService subjectService, UserService userService) {
        this.teacherService = teacherService;
        this.classService = classService;
        this.studentService = studentService;
        this.parentService = parentService;
        this.gradeService = gradeService;
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String viewDashboard() {
        return "teacher/teacher_dashboard";
    }

    @GetMapping("/view-classes")
    public String viewClasses(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> currentUserOptional = userService.findByUsername(currentUsername);

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            if (currentUser instanceof Teacher teacher) {
                List<Class> classes = classService.findClassesByTeacher(teacher);
                model.addAttribute("classes", classes);
            }
        }
        return "teacher/view_classes";
    }

    //
//    @GetMapping("/view-class/{id}")
//    public String viewClass(@PathVariable("id") Long classId, Model model) {
//        Optional<Class> classesOptional = classService.findById(classId);
//        if (classesOptional.isPresent()) {
//            Class classes = classesOptional.get();
//            List<Student> students = classes.getStudents();
//            model.addAttribute("students", students);
//        }
//        return "view_class";
//    }
//
    @GetMapping("/view-students")
    public String viewStudents(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> currentUserOptional = userService.findByUsername(currentUsername);

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            if (currentUser instanceof Teacher teacher) {
                List<Student> students = studentService.findStudentsByTeacherOrdered(teacher);
                model.addAttribute("students", students);
            }
        }

        return "teacher/view_students";
    }
//
//    @GetMapping("/view-parents")
//    public String viewParents(Model model) {
//        List<Parent> parents = parentService.findAllParents();
//        model.addAttribute("parents", parents);
//        return "view_parents";
//    }
//
    @GetMapping("/view-teachers")
    public String viewTeachers(Model model) {
        List<Teacher> teachers = teacherService.findAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teacher/view_teachers";
    }
//
//    @GetMapping("/profile")
//    public String viewProfile(Model model) {
//        User currentUser = userService.loadUserByUsername("currentUsername"); // Replace with actual logged-in user retrieval
//        if (currentUser instanceof Teacher) {
//            Teacher teacher = (Teacher) currentUser;
//            model.addAttribute("user", teacher);
//        }
//        return "view_profile";
//    }
//
//    @GetMapping("/add-grade/{id}")
//    public String addGrade(@PathVariable("id") Long studentId, Model model) {
//        Optional<Student> studentOptional = studentService.findById(studentId);
//        if (studentOptional.isPresent()) {
//            Student student = studentOptional.get();
//            model.addAttribute("student", student);
//            model.addAttribute("subject", new Subject()); // Adjust as necessary to get the actual subject
//        }
//        return "add_grade";
//    }
//
//    @PostMapping("/add-grade")
//    public String saveGrade(@RequestParam("grade") Double grade,
//                            @RequestParam("description") String description,
//                            @RequestParam("studentId") Long studentId,
//                            @RequestParam("subjectId") Long subjectId) {
//        Grade newGrade = new Grade();
//        newGrade.setGrade(grade);
//        newGrade.setDescription(description);
//        Optional<Student> studentOptional = studentService.findById(studentId);
//        if (studentOptional.isPresent()) {
//            newGrade.setStudent(studentOptional.get());
//        }
//        Optional<Subject> subjectOptional = subjectService.findById(subjectId);
//        if (subjectOptional.isPresent()) {
//            newGrade.setSubject(subjectOptional.get());
//        }
//        gradeService.saveGrade(newGrade); // Ensure saveGrade method exists in GradeService
//        return "redirect:/teacher/view-students";
//    }
//


    @GetMapping("/logout-confirmation")
    public String logoutConfirmation() {
        return "teacher/logout_confirmation";
    }
}
