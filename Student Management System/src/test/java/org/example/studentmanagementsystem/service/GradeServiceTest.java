package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradeService gradeService;

    private Grade grade;
    private List<Grade> grades;
    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        grade = new Grade();
        grade.setGradeId(1L);

        student = new Student();
        student.setUserId(1L);
        student.setUsername("teststudent");

        subject = new Subject();
        subject.setSubjectId(1L);
        subject.setSubjectName("Math");

        grades = Arrays.asList(grade);
    }

    @Test
    void save_GradeSaved() {
        gradeService.save(grade);
        verify(gradeRepository, times(1)).save(grade);
    }

    @Test
    void findById_GradeExists() {
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));
        Optional<Grade> foundGrade = gradeService.findById(1L);
        assertTrue(foundGrade.isPresent());
        assertEquals(grade.getGradeId(), foundGrade.get().getGradeId());
    }

    @Test
    void findById_GradeDoesNotExist() {
        when(gradeRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Grade> foundGrade = gradeService.findById(1L);
        assertFalse(foundGrade.isPresent());
    }

    @Test
    void delete_GradeDeleted() {
        gradeService.delete(grade);
        verify(gradeRepository, times(1)).delete(grade);
    }

    @Test
    void findByStudentAndSubject_GradesExist() {
        when(gradeRepository.findByStudentAndSubject(student, subject)).thenReturn(grades);
        List<Grade> foundGrades = gradeService.findByStudentAndSubject(student, subject);
        assertNotNull(foundGrades);
        assertEquals(1, foundGrades.size());
    }
}
