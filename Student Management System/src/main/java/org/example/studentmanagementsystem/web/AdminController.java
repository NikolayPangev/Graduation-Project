package org.example.studentmanagementsystem.web;

import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.studentmanagementsystem.model.dtos.ParentForm;
import org.example.studentmanagementsystem.model.dtos.StudentForm;
import org.example.studentmanagementsystem.model.dtos.TeacherForm;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.repository.ClassRepository;
import org.example.studentmanagementsystem.service.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final StudentService studentService;
    private final ParentService parentService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final ClassRepository classRepository;
    private final SubjectService subjectService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, ParentService parentService, StudentService studentService, TeacherService teacherService, ClassService classService, ClassRepository classRepository, SubjectService subjectService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.parentService = parentService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.classService = classService;
        this.classRepository = classRepository;
        this.subjectService = subjectService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "admin/admin_dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "admin/view_profile";
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

        String successMessage = "Student " + studentForm.getFirstName() + " " + studentForm.getLastName() + " was successfully registered!";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

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

    @GetMapping("/createTeacher")
    public String createTeacherForm(Model model) {
        model.addAttribute("teacherForm", new TeacherForm());
        return "admin/register_teacher";
    }

    @PostMapping("/createTeacher")
    public String createTeacher(@ModelAttribute @Validated TeacherForm teacherForm,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (userService.usernameExists(teacherForm.getUsername())) {
            result.rejectValue("username", "error.username", "Username is already taken");
        }

        if (userService.emailExists(teacherForm.getEmail())) {
            result.rejectValue("email", "error.email", "Email is already taken");
        }

        if (!teacherForm.getPassword().equals(teacherForm.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "admin/register_teacher";
        }

        userService.createTeacher(teacherForm);

        String successMessage = "Teacher " + teacherForm.getFirstName() + " " + teacherForm.getLastName() + " was successfully registered!";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:/admin/createTeacher";
    }

    @GetMapping("/viewTeachers")
    public String viewTeachers(Model model) {
        List<Teacher> teachers = teacherService.findAllTeachers();
        List<Subject> subjects = subjectService.findAllSubjects();
        List<Class> classes = classService.findAllClasses();
        model.addAttribute("teachers", teachers);
        model.addAttribute("subjects", subjects);
        model.addAttribute("classes", classes);
        return "admin/view_teachers";
    }

    @PostMapping("/assignSubjectToTeacher")
    public String assignSubjectToTeacher(
            @RequestParam Long teacherId,
            @RequestParam Long subjectId,
            RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            Subject subject = subjectService.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));

            teacher.getSubjects().forEach(s -> {
                s.setTeacher(null);
                subjectService.save(s);
            });

            teacher.getSubjects().clear();
            teacher.getSubjects().add(subject);
            subject.setTeacher(teacher);
            subjectService.save(subject);

            String successMessage = "Successfully assigned subject " + subject.getSubjectName() + " to teacher " + teacher.getFirstName() + " " + teacher.getLastName();
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "Couldn't assign subject to teacher: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/viewTeachers";
    }

    @PostMapping("/assignClassToTeacher")
    public String assignClassToTeacher(
            @RequestParam Long teacherId,
            @RequestParam List<Long> classIds,
            RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            if (teacher.getSubjects().isEmpty()) {
                throw new RuntimeException("Teacher is not assigned to any subject");
            }
            Subject subject = teacher.getSubjects().get(0);

            // Retrieve the classes by their IDs
            List<Class> classList = classService.findByIds(classIds);
            for (Class clazz : classList) {
                Teacher currentTeacher = clazz.getTeachers().stream()
                        .filter(t -> t.getSubjects().contains(subject))
                        .findFirst().orElse(null);

                if (currentTeacher != null && !currentTeacher.equals(teacher)) {
                    currentTeacher.getClasses().remove(clazz);
                    clazz.getTeachers().remove(currentTeacher);
                    teacherService.save(currentTeacher);
                    classService.save(clazz);
                }

                teacher.getClasses().add(clazz);
                clazz.getTeachers().add(teacher);
                clazz.setSubject(subject);
                teacherService.save(teacher);
                classService.save(clazz);
            }

            StringBuilder successMessage = new StringBuilder("Successfully assigned classes ");
            for (Class clazz : classList) {
                successMessage.append(clazz.getGrade()).append("-").append(clazz.getSection()).append(", ");
            }
            successMessage = new StringBuilder(successMessage.substring(0, successMessage.length() - 2) +
                    " to teacher " + teacher.getFirstName() + " " + teacher.getLastName());
            redirectAttributes.addFlashAttribute("successMessage", successMessage.toString());
        } catch (Exception e) {
            String errorMessage = "Couldn't assign classes to teacher: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/viewTeachers";
    }


    @PostMapping("/deleteTeacher")
    public String deleteTeacher(@RequestParam Long teacherId, RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            teacherService.deleteTeacher(teacherId);

            String successMessage = "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + " successfully deleted";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "Error deleting teacher: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/viewTeachers";
    }

    @GetMapping("/createParent")
    public String createParentForm(Model model) {
        model.addAttribute("parentForm", new ParentForm());
        return "admin/register_parent";
    }

    @PostMapping("/createParent")
    public String createParent(@ModelAttribute @Validated ParentForm parentForm,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (userService.usernameExists(parentForm.getUsername())) {
            result.rejectValue("username", "error.username", "Username is already taken");
        }

        if (userService.emailExists(parentForm.getEmail())) {
            result.rejectValue("email", "error.email", "Email is already taken");
        }

        if (!parentForm.getPassword().equals(parentForm.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "admin/register_parent";
        }

        userService.createParent(parentForm);

        String successMessage = "Parent " + parentForm.getFirstName() + " " + parentForm.getLastName() + " was successfully registered!";
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:/admin/createParent";
    }

    @GetMapping("/viewParents")
    public String viewParents(Model model) {
        List<Parent> parents = parentService.findAllParents();
        model.addAttribute("parents", parents);
        model.addAttribute("students", studentService.findAllStudents());
        return "admin/view_parents";
    }

    @PostMapping("/assignStudentsToParent")
    public String assignStudentsToParent(
            @RequestParam Long parentId,
            @RequestParam List<Long> studentIds,
            RedirectAttributes redirectAttributes) {
        try {
            Parent parent = parentService.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));

            List<Student> studentList = studentService.findByIds(studentIds);
            parent.getChildren().clear();
            parent.getChildren().addAll(studentList);

            for (Student student : studentList) {
                student.setParent(parent);
                studentService.save(student);
            }

            parentService.save(parent);

            StringBuilder successMessage = new StringBuilder("Successfully assigned ");
            for (Student student : studentList) {
                successMessage.append(student.getFirstName()).append(" ").append(student.getLastName()).append(", ");
            }
            successMessage = new StringBuilder(successMessage.substring(0, successMessage.length() - 2) +
                    " to parent " + parent.getFirstName() + " " + parent.getLastName());
            redirectAttributes.addFlashAttribute("successMessage", successMessage.toString());
        } catch (Exception e) {
            String errorMessage = "Couldn't assign students to parent: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/viewParents";
    }

    @PostMapping("/deleteParent")
    public String deleteParent(@RequestParam Long parentId, RedirectAttributes redirectAttributes) {
        try {
            Parent parent = parentService.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            parentService.deleteParent(parentId);

            String successMessage = "Parent " + parent.getFirstName() + " " + parent.getLastName() + " successfully deleted";
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = "Error deleting parent: " + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/admin/viewParents";
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
