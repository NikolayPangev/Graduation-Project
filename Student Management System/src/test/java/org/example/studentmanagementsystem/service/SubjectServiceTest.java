package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.SubjectRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject subject;
    private List<Subject> subjects;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        subject.setSubjectId(1L);
        subject.setSubjectName("Mathematics");

        subjects = Arrays.asList(subject);
    }

    @Test
    void existsByName_SubjectExists() {
        when(subjectRepository.findBySubjectName("Mathematics")).thenReturn(Optional.of(subject));
        assertTrue(subjectService.existsByName("Mathematics"));
    }

    @Test
    void existsByName_SubjectDoesNotExist() {
        when(subjectRepository.findBySubjectName("Science")).thenReturn(Optional.empty());
        assertFalse(subjectService.existsByName("Science"));
    }

    @Test
    void createSubject_SubjectCreated() {
        subjectService.createSubject("Mathematics");
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void findAllSubjects_SubjectsExist() {
        when(subjectRepository.findAll()).thenReturn(subjects);
        List<Subject> foundSubjects = subjectService.findAllSubjects();
        assertNotNull(foundSubjects);
        assertEquals(1, foundSubjects.size());
    }

    @Test
    void findById_SubjectExists() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        Optional<Subject> foundSubject = subjectService.findById(1L);
        assertTrue(foundSubject.isPresent());
        assertEquals(subject.getSubjectId(), foundSubject.get().getSubjectId());
    }

    @Test
    void findById_SubjectDoesNotExist() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Subject> foundSubject = subjectService.findById(1L);
        assertFalse(foundSubject.isPresent());
    }

    @Test
    void findByTeacherId_SubjectExists() {
        when(subjectRepository.findByTeacher_UserId(1L)).thenReturn(subject);
        Subject foundSubject = subjectService.findByTeacherId(1L);
        assertNotNull(foundSubject);
        assertEquals(subject.getSubjectName(), foundSubject.getSubjectName());
    }

    @Test
    void save_SubjectSaved() {
        subjectService.save(subject);
        verify(subjectRepository, times(1)).save(subject);
    }

    @Test
    void deleteSubject_SubjectDeleted() {
        subjectService.deleteSubject(1L);
        verify(subjectRepository, times(1)).deleteById(1L);
    }
}
