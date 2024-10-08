package org.example.studentmanagementsystem.web;

import jakarta.validation.Valid;
import org.example.studentmanagementsystem.model.dtos.ParentForm;
import org.example.studentmanagementsystem.model.dtos.StudentForm;
import org.example.studentmanagementsystem.model.dtos.TeacherForm;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.repository.ClassRepository;
import org.example.studentmanagementsystem.service.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;

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

    public AdminController(UserService userService,
                           ParentService parentService,
                           StudentService studentService,
                           TeacherService teacherService,
                           ClassService classService,
                           ClassRepository classRepository,
                           SubjectService subjectService) {
        this.userService = userService;
        this.parentService = parentService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.classService = classService;
        this.classRepository = classRepository;
        this.subjectService = subjectService;
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        long totalStudents = studentService.countStudents();
        long totalTeachers = teacherService.countTeachers();
        long totalParents = parentService.countParents();
        long totalClasses = classService.countClasses();

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalParents", totalParents);
        model.addAttribute("totalClasses", totalClasses);

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
        model.addAttribute("students", studentService.findAllStudents());
        model.addAttribute("classes", classService.getAllClassesOrderedByGradeAndSection());
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
        validateUserForm(studentForm.getUsername(), studentForm.getEmail(), studentForm.getPassword(), studentForm.getConfirmPassword(), result);
        if (result.hasErrors()) {
            return "admin/register_student";
        }
        try {
            userService.createStudent(studentForm); redirectAttributes.addFlashAttribute("successMessage", "Student " + studentForm.getFirstName() + " " + studentForm.getLastName() + " was successfully registered!");
            return "redirect:/admin/createStudent";
        } catch (Exception e) {
            result.reject("error.global", "An unexpected error occurred while creating the student.");
            return "admin/register_student";
        }
    }

    private void validateUserForm(String username, String email, String password, String confirmPassword, BindingResult result) {
        if (userService.usernameExists(username)) {
            result.rejectValue("username", "error.username", "Username is already taken");
        }
        if (userService.emailExists(email)) {
            result.rejectValue("email", "error.email", "Email is already taken");
        }
        if (!password.equals(confirmPassword)) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }
    }

    @PostMapping("/assignClassToStudent")
    public String assignClassToStudent(@RequestParam Long studentId, @RequestParam Long classId, RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Class classes = classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            student.setClasses(classes);
            studentService.save(student);

            redirectAttributes.addFlashAttribute("successMessage", "Class successfully assigned to student.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error assigning class: " + e.getMessage());
        }

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
        validateUserForm(teacherForm.getUsername(), teacherForm.getEmail(), teacherForm.getPassword(), teacherForm.getConfirmPassword(), result);
        if (result.hasErrors()) {
            return "admin/register_teacher";
        }
        userService.createTeacher(teacherForm);
        redirectAttributes.addFlashAttribute("successMessage", "Teacher " + teacherForm.getFirstName() + " " + teacherForm.getLastName() + " was successfully registered!");
        return "redirect:/admin/createTeacher";
    }

    @GetMapping("/viewTeachers")
    public String viewTeachers(Model model) {
        model.addAttribute("teachers", teacherService.findAllTeachers());
        model.addAttribute("subjects", subjectService.findAllSubjects());
        model.addAttribute("classes", classService.findAllClasses());
        return "admin/view_teachers";
    }

    @PostMapping("/assignSubjectToTeacher")
    public String assignSubjectToTeacher(@RequestParam Long teacherId,
                                         @RequestParam Long subjectId,
                                         RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            Subject newSubject = subjectService.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));

            Subject previousSubject = teacher.getSubject();
            if (previousSubject != null) {
                previousSubject.getTeacher().remove(teacher);
                subjectService.save(previousSubject);
            }

            teacher.setSubject(newSubject);
            newSubject.getTeacher().add(teacher);

            teacherService.save(teacher);
            subjectService.save(newSubject);

            redirectAttributes.addFlashAttribute("successMessage", "Successfully assigned subject " + newSubject.getSubjectName() + " to teacher " + teacher.getFirstName() + " " + teacher.getLastName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Couldn't assign subject to teacher: " + e.getMessage());
        }
        return "redirect:/admin/viewTeachers";
    }

    @PostMapping("/assignClassToTeacher")
    public String assignClassToTeacher(@RequestParam Long teacherId,
                                       @RequestParam List<Long> classIds,
                                       RedirectAttributes redirectAttributes) {
        try {
            Teacher newTeacher = teacherService.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            Subject newSubject = newTeacher.getSubject();
            if (newSubject == null) {
                throw new RuntimeException("Teacher is not assigned to any subject");
            }

            for (Long classId : classIds) {
                Class clazz = classService.findById(classId)
                        .orElseThrow(() -> new RuntimeException("Class not found"));

                Iterator<Teacher> iterator = clazz.getTeachers().iterator();
                while (iterator.hasNext()) {
                    Teacher existingTeacher = iterator.next();
                    if (existingTeacher.getSubject() != null && existingTeacher.getSubject().equals(newSubject)) {
                        iterator.remove();
                        existingTeacher.getClasses().remove(clazz);
                        teacherService.save(existingTeacher);
                    }
                }

                if (!clazz.getTeachers().contains(newTeacher)) {
                    clazz.getTeachers().add(newTeacher);
                    newTeacher.getClasses().add(clazz);
                }

                classService.save(clazz);
                teacherService.save(newTeacher);
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully assigned classes to teacher " + newTeacher.getFirstName() + " " + newTeacher.getLastName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Couldn't assign classes to teacher: " + e.getMessage());
        }
        return "redirect:/admin/viewTeachers";
    }

    @PostMapping("/removeClassFromTeacher")
    public String removeClassFromTeacher(@RequestParam Long teacherId,
                                         @RequestParam Long classId,
                                         RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            Class clazz = classService.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            Class classToRemove = null;
            for (Class assignedClass : teacher.getClasses()) {
                if (assignedClass.getClassId().equals(clazz.getClassId())) {
                    classToRemove = assignedClass;
                    break;
                }
            }

            if (classToRemove != null) {
                teacher.getClasses().remove(classToRemove);

                clazz.getTeachers().remove(teacher);

                teacherService.save(teacher);
                classService.save(clazz);

                redirectAttributes.addFlashAttribute("successMessage",
                        "Class " + clazz.getGrade() + "-" + clazz.getSection() + " successfully removed from teacher " + teacher.getFirstName() + " " + teacher.getLastName());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() + " is not assigned to class " + clazz.getGrade() + "-" + clazz.getSection());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error removing class from teacher: " + e.getMessage());
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
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Teacher cannot be deleted because they have classes assigned to them.";
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
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

            if (!parent.getChildren().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "A parent cannot be deleted because they have a child assigned to them.");
                return "redirect:/admin/viewParents";
            }

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

    @GetMapping("/viewClass/{id}")
    public String viewClass(@PathVariable Long id, Model model) {
        List<Student> students = studentService.findStudentsByClassId(id);
        model.addAttribute("students", students);
        model.addAttribute("classId", id);
        return "admin/view_class";
    }

    @PostMapping("/deleteClass")
    public String deleteClass(@RequestParam Long classId, RedirectAttributes redirectAttributes) {
        try {
            classService.deleteClass(classId);
            redirectAttributes.addFlashAttribute("successMessage", "The class has been successfully deleted");
        } catch (Exception e) {
            if (e.getMessage().contains("a foreign key constraint fails")) {
                redirectAttributes.addFlashAttribute("errorMessage", "The class cannot be deleted because it has students or a teachers assigned to it.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error deleting class: " + e.getMessage());
            }
        }
        return "redirect:/admin/viewClasses";
    }

    @GetMapping("/createSubject")
    public String showCreateSubjectForm(Model model) {
        model.addAttribute("errorMessage", null);
        model.addAttribute("successMessage", null);
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
        return "admin/view_subjects";
    }

    @PostMapping("/deleteSubject")
    public String deleteSubject(@RequestParam Long subjectId, RedirectAttributes redirectAttributes) {
        try {
            subjectService.deleteSubject(subjectId);
            redirectAttributes.addFlashAttribute("successMessage", "Subject deleted successfully");
        } catch (Exception e) {
            if (e.getMessage().contains("a foreign key constraint fails")) {
                redirectAttributes.addFlashAttribute("errorMessage", "The subject cannot be deleted because it has students or a teachers assigned to it.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error deleting class: " + e.getMessage());
            }
        }
        return "redirect:/admin/viewSubjects";
    }

    @GetMapping("/logoutConfirmation")
    public String logoutConfirmation() {
        return "admin/logout_confirmation";
    }
}
