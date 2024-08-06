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

import java.security.Principal;
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

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            if (currentUser instanceof Teacher teacher) {
                List<Class> classes = classService.findClassesByTeacher(teacher);
                model.addAttribute("classes", classes);
            }
        }
        return "teacher/view_classes";
    }


    @GetMapping("/view-class/{id}")
    public String viewClass(@PathVariable("id") Long classId, Model model) {
        Optional<Class> classOptional = classService.findById(classId);
        if (classOptional.isPresent()) {
            Class classes = classOptional.get();
            Set<Student> studentSet = classes.getStudents();
            List<Student> students = new ArrayList<>(studentSet);
            // Sorting students alphabetically by first, middle, and last name
            students.sort(Comparator.comparing(Student::getFirstName)
                    .thenComparing(Student::getMiddleName)
                    .thenComparing(Student::getLastName));
            model.addAttribute("students", students);
            model.addAttribute("class", classes);
        } else {
            model.addAttribute("errorMessage", "Class not found");
            return "error";
        }
        return "teacher/view_class"; // Ensure the correct view name is returned
    }



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

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUserOptional = userService.findByUsername(currentUsername);

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            model.addAttribute("user", currentUser);
        } else {
            model.addAttribute("errorMessage", "User not found.");
        }

        return "teacher/view_profile";
    }

    @GetMapping("/add-grade/{studentId}")
    public String showAddGradeForm(@PathVariable Long studentId, Model model) {
        // Fetch the student
        Optional<Student> optionalStudent = studentService.findById(studentId);
        if (optionalStudent.isEmpty()) {
            model.addAttribute("errorMessage", "Student not found");
            return "error";
        }
        Student student = optionalStudent.get();

        // Prepare the GradeForm
        GradeForm gradeForm = new GradeForm();
        gradeForm.setStudentId(studentId);

        // Fetch the teacher and subject
        Long currentTeacherId = getCurrentTeacherId();
        Subject subject = subjectService.findByTeacherId(currentTeacherId);
        if (subject != null) {
            model.addAttribute("subject", subject);
        } else {
            model.addAttribute("errorMessage", "Subject not found for the teacher");
            return "error";
        }

        model.addAttribute("gradeForm", gradeForm);
        return "teacher/add_grade"; // The HTML form view name
    }

    @PostMapping("/add-grade")
    public String addGrade(@ModelAttribute GradeForm gradeForm, Model model) {

        // Fetch the teacher and subject
        Long currentTeacherId = getCurrentTeacherId();
        Subject subject = subjectService.findByTeacherId(currentTeacherId);

        if (subject == null) {
            model.addAttribute("errorMessage", "Subject not found for the teacher");
            return "error";
        }

        // Fetch the student
        Optional<Student> optionalStudent = studentService.findById(gradeForm.getStudentId());
        if (optionalStudent.isEmpty()) {
            model.addAttribute("errorMessage", "Student not found");
            return "error";
        }
        Student student = optionalStudent.get();

        // Add the grade
        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setGrade(gradeForm.getGrade());
        grade.setDescription(gradeForm.getDescription());
        grade.setDateGiven(LocalDate.now()); // Set the current date

        gradeService.save(grade);

        // Redirect to the view class page with the classId parameter
        Long classId = student.getClasses().getClassId();
        return "redirect:/teacher/view-class/" + classId;
    }


    private Long getCurrentTeacherId() {
        // Get the username of the current authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Fetch the Teacher object based on the username
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher != null) {
            return teacher.getUserId();
        } else {
            throw new RuntimeException("Teacher not found");
        }
    }

    @GetMapping("/logout-confirmation")
    public String logoutConfirmation() {
        return "teacher/logout_confirmation";
    }
}
