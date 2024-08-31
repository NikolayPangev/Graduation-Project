package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.GradeForm;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.service.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        currentUserOptional.ifPresent(currentUser -> {
            if (currentUser instanceof Teacher teacher) {
                List<Class> classes = classService.findClassesByTeacher(teacher);
                model.addAttribute("classes", classes);
            }
        });
        return "teacher/view_classes";
    }

    @GetMapping("/view-class/{id}")
    public String viewClass(@PathVariable("id") Long classId, Model model) {
        Optional<Class> classOptional = classService.findById(classId);
        if (classOptional.isPresent()) {
            Class classes = classOptional.get();
            List<Student> students = new ArrayList<>(classes.getStudents());
            students.sort(Comparator.comparing(Student::getFirstName)
                    .thenComparing(Student::getMiddleName)
                    .thenComparing(Student::getLastName));
            model.addAttribute("students", students);
            model.addAttribute("class", classes);
        } else {
            model.addAttribute("errorMessage", "Class not found");
            return "error";
        }
        return "teacher/view_class";
    }

    @GetMapping("/view-students")
    public String viewStudents(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUserOptional = userService.findByUsername(currentUsername);

        currentUserOptional.ifPresent(currentUser -> {
            if (currentUser instanceof Teacher teacher) {
                List<Student> students = studentService.findStudentsByTeacherOrdered(teacher);
                model.addAttribute("students", students);
            }
        });
        return "teacher/view_students";
    }

    @GetMapping("/view-parents")
    public String viewParents(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUserOptional = userService.findByUsername(currentUsername);

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();

            if (currentUser instanceof Teacher teacher) {

                List<Class> classes = classService.findClassesByTeacher(teacher);

                List<Student> students = classes.stream()
                        .flatMap(cls -> studentService.findStudentsByClass(cls).stream())
                        .toList();

                Map<String, Parent> uniqueParentsMap = students.stream()
                        .flatMap(student -> parentService.findParentsByStudent(student).stream())
                        .collect(Collectors.toMap(
                                Parent::getUsername,
                                parent -> parent,
                                (existing, replacement) -> existing
                        ));

                List<Parent> uniqueParentsList = new ArrayList<>(uniqueParentsMap.values());
                uniqueParentsList.sort(Comparator.comparing(Parent::getLastName)
                        .thenComparing(Parent::getFirstName)
                        .thenComparing(Parent::getMiddleName));

                model.addAttribute("parents", uniqueParentsList);
            }
        }
        return "teacher/view_parents";
    }

    @GetMapping("/view-teachers")
    public String viewTeachers(Model model) {
        List<Teacher> teachers = teacherService.findAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teacher/view_teachers";
    }

    @GetMapping("/add-grade/{studentId}")
    public String showAddGradeForm(@PathVariable Long studentId, Model model) {
        try {
            // Fetch the student by ID
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

            // Create a new GradeForm object and populate it with student information
            GradeForm gradeForm = new GradeForm();
            gradeForm.setStudentId(studentId);

            // Get the currently logged-in teacher's ID
            Long teacherId = getCurrentTeacherId();

            // Fetch the subject taught by the current teacher
            Subject subject = subjectService.findByTeacherId(teacherId);
            if (subject == null) {
                model.addAttribute("errorMessage", "No subject assigned to the teacher.");
                return "error"; // Return an error page if no subject is found
            }

            // Add the necessary attributes to the model
            model.addAttribute("student", student);
            model.addAttribute("subject", subject);
            model.addAttribute("gradeForm", gradeForm);

            // Return the view for adding a grade
            return "teacher/add_grade";
        } catch (IllegalArgumentException e) {
            // If any exception occurs, show an error page
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }


    @PostMapping("/add-grade")
    public String addGrade(@ModelAttribute GradeForm gradeForm, Model model) {
        Long currentTeacherId = getCurrentTeacherId();
        Subject subject = subjectService.findByTeacherId(currentTeacherId);

        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found for the teacher");
            return "error";
        }

        Optional<Student> optionalStudent = studentService.findById(gradeForm.getStudentId());
        if (optionalStudent.isEmpty()) {
            model.addAttribute("errorMessage", "Student not found");
            return "error";
        }
        Student student = optionalStudent.get();

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setGrade(gradeForm.getGrade());
        grade.setDescription(gradeForm.getDescription());
        grade.setDateGiven(LocalDate.now());
        gradeService.save(grade);

        Long classId = student.getClasses().getClassId();
        return "redirect:/teacher/view-class/" + classId;
    }

    @PostMapping("/delete-grade/{gradeId}")
    public String deleteGrade(@PathVariable Long gradeId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Grade> gradeOptional = gradeService.findById(gradeId);
        if (gradeOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Grade not found");
            return "error";
        }
        Grade grade = gradeOptional.get();
        Long classId = grade.getStudent().getClasses().getClassId();
        gradeService.delete(grade);
        redirectAttributes.addFlashAttribute("message", "Grade deleted successfully");
        return "redirect:/teacher/view-class/" + classId;
    }

    private Long getCurrentTeacherId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher != null) {
            return teacher.getUserId();
        } else {
            throw new RuntimeException("Teacher not found");
        }
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUserOptional = userService.findByUsername(currentUsername);

        currentUserOptional.ifPresentOrElse(
                currentUser -> model.addAttribute("user", currentUser),
                () -> model.addAttribute("errorMessage", "User not found.")
        );
        return "teacher/view_profile";
    }

    @GetMapping("/logout-confirmation")
    public String logoutConfirmation() {
        return "teacher/logout_confirmation";
    }
}
