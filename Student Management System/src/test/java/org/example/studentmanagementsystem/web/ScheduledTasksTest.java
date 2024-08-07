package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.entities.Parent;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.service.ParentService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

@SpringBootTest
@EnableScheduling
@ActiveProfiles("test")
class ScheduledTasksTest {

    @MockBean
    private StudentService studentService;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private ParentService parentService;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @SpyBean
    private ScheduledTasks scheduledTasks;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDailyStudentCheck() {
        List<Student> students = Arrays.asList(new Student(), new Student());
        when(studentService.findAllStudents()).thenReturn(students);

        scheduledTasks.dailyStudentCheck();

        verify(studentService, times(1)).findAllStudents();
        verify(scheduledTasks, times(1)).dailyStudentCheck();
    }

    @Test
    void testDailyTeacherCheck() {
        List<Teacher> teachers = Arrays.asList(new Teacher(), new Teacher());
        when(teacherService.findAllTeachers()).thenReturn(teachers);

        scheduledTasks.dailyTeacherCheck();

        verify(teacherService, times(1)).findAllTeachers();
        verify(scheduledTasks, times(1)).dailyTeacherCheck();
    }

    @Test
    void testDailyParentCheck() {
        List<Parent> parents = Arrays.asList(new Parent(), new Parent());
        when(parentService.findAllParents()).thenReturn(parents);

        scheduledTasks.dailyParentCheck();

        verify(parentService, times(1)).findAllParents();
        verify(scheduledTasks, times(1)).dailyParentCheck();
    }

    @Test
    void testScheduledMethods() throws InterruptedException {
        String cronExpression = "0/5 * * * * ?";

        ScheduledFuture<?> studentCheckFuture = taskScheduler.schedule(
                scheduledTasks::dailyStudentCheck,
                new CronTrigger(cronExpression)
        );
        ScheduledFuture<?> teacherCheckFuture = taskScheduler.schedule(
                scheduledTasks::dailyTeacherCheck,
                new CronTrigger(cronExpression)
        );
        ScheduledFuture<?> parentCheckFuture = taskScheduler.schedule(
                scheduledTasks::dailyParentCheck,
                new CronTrigger(cronExpression)
        );

        Thread.sleep(15000);

        verify(scheduledTasks, atLeastOnce()).dailyStudentCheck();
        verify(scheduledTasks, atLeastOnce()).dailyTeacherCheck();
        verify(scheduledTasks, atLeastOnce()).dailyParentCheck();

        studentCheckFuture.cancel(true);
        teacherCheckFuture.cancel(true);
        parentCheckFuture.cancel(true);
    }
}
