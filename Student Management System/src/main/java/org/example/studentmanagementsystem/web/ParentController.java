package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.mapper.StudentMapper;
import org.example.studentmanagementsystem.model.dtos.SubjectAbsencesDTO;
import org.example.studentmanagementsystem.model.dtos.SubjectWithGrades;
import org.example.studentmanagementsystem.model.dtos.StudentDTO;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parent")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final AbsenceService absenceService;
    private final FeedbackService feedbackService;
    private final ClassScheduleServiceImpl scheduleService;


    public ParentController(ParentService parentService,
                            StudentService studentService,
                            GradeService gradeService,
                            AbsenceService absenceService,
                            FeedbackService feedbackService,
                            ClassScheduleServiceImpl scheduleService) {
        this.parentService = parentService;
        this.studentService = studentService;
        this.gradeService = gradeService;
        this.absenceService = absenceService;
        this.feedbackService = feedbackService;
        this.scheduleService = scheduleService;
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
        Set<Teacher> teachers = studentClass.getTeachers();

        model.addAttribute("teachers", teachers);
        model.addAttribute("child", child);
        return "parent/view_teachers";
    }

    @GetMapping("/classmates/{childId}")
    public String viewClassmates(@PathVariable Long childId, Model model) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        Class studentClass = child.getClasses();
        List<Student> classmates = studentService.findByClass(studentClass);

        model.addAttribute("classmates", classmates);
        return "parent/view_classmates";
    }

    @GetMapping("/child-absences/{childId}")
    public String childAbsences(@PathVariable Long childId, Model model) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        List<SubjectAbsencesDTO> subjectAbsencesList = absenceService.findAbsencesByStudent(child);

        model.addAttribute("subjectAbsences", subjectAbsencesList);
        model.addAttribute("child", child);
        return "parent/view_absences";
    }

    @GetMapping("/child-feedback/{childId}")
    public String childFeedback(@PathVariable Long childId, Model model) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        List<Feedback> feedbacks = feedbackService.findFeedbackByStudent(child);
        Map<Subject, List<Feedback>> feedbackBySubject = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getSubject));

        model.addAttribute("feedbackBySubject", feedbackBySubject);
        model.addAttribute("child", child);
        return "parent/view_feedback";
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

    // ParentController (for a particular child)
    @GetMapping("/view-schedule/{childId}")
    public String parentViewChildSchedule(@PathVariable Long childId, Model model, Principal principal) {
        Student child = studentService.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        Class cls = child.getClasses();

        List<String> slots = List.of("08:00-09:30", "09:50-11:20", "12:20-13:50", "14:10-15:40");
        List<DayOfWeek> days = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
        );

        // Build timetable map (day -> slot -> ClassSchedule)
        Map<DayOfWeek, Map<String, ClassSchedule>> timetable = new LinkedHashMap<>();
        for (DayOfWeek day : days) {
            Map<String, ClassSchedule> row = new LinkedHashMap<>();
            for (String slot : slots) {
                LocalTime start = LocalTime.parse(slot.split("-")[0]);
                Optional<ClassSchedule> existing = scheduleService.findByClassAndSlot(cls, day, start);
                row.put(slot, existing.orElse(null));
            }
            timetable.put(day, row);
        }

        // Build display map (day_slot -> subjectName or "-")
        Map<String, String> displayCells = new HashMap<>();
        for (DayOfWeek day : days) {
            for (String slot : slots) {
                ClassSchedule cs = timetable.get(day).get(slot);
                String key = day.name() + "_" + slot.replace(":", "").replace("-", "_");
                if (cs != null && cs.getSubject() != null) {
                    displayCells.put(key, cs.getSubject().getSubjectName());
                } else {
                    displayCells.put(key, "-");
                }
            }
        }

        model.addAttribute("classObj", cls);
        model.addAttribute("child", child);
        model.addAttribute("slots", slots);
        model.addAttribute("days", days);
        model.addAttribute("displayCells", displayCells);

        return "parent/view_schedule";
    }

}
