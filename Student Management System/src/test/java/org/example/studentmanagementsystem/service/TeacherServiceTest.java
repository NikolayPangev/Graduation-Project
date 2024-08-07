package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.repository.TeacherRepository;
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
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;
    private List<Teacher> teachers;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setUserId(1L);
        teacher.setUsername("testteacher");

        teachers = Arrays.asList(teacher);
    }

    @Test
    void findById_TeacherExists() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        Optional<Teacher> foundTeacher = teacherService.findById(1L);
        assertTrue(foundTeacher.isPresent());
        assertEquals(teacher.getUserId(), foundTeacher.get().getUserId());
    }

    @Test
    void findById_TeacherDoesNotExist() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Teacher> foundTeacher = teacherService.findById(1L);
        assertFalse(foundTeacher.isPresent());
    }

    @Test
    void findByClassId_TeachersExist() {
        when(teacherRepository.findByClasses_ClassId(1L)).thenReturn(teachers);
        List<Teacher> foundTeachers = teacherService.findByClassId(1L);
        assertNotNull(foundTeachers);
        assertEquals(1, foundTeachers.size());
        assertEquals(teacher.getUsername(), foundTeachers.get(0).getUsername());
    }

    @Test
    void findAllTeachers_TeachersExist() {
        when(teacherRepository.findAll()).thenReturn(teachers);
        List<Teacher> foundTeachers = teacherService.findAllTeachers();
        assertNotNull(foundTeachers);
        assertEquals(1, foundTeachers.size());
    }

    @Test
    void findByUsername_TeacherExists() {
        when(teacherRepository.findByUsername("testteacher")).thenReturn(teacher);
        Teacher foundTeacher = teacherService.findByUsername("testteacher");
        assertNotNull(foundTeacher);
        assertEquals(teacher.getUsername(), foundTeacher.getUsername());
    }

    @Test
    void save_TeacherSaved() {
        teacherService.save(teacher);
        verify(teacherRepository, times(1)).save(teacher);
    }

    @Test
    void deleteTeacher_TeacherDeleted() {
        teacherService.deleteTeacher(1L);
        verify(teacherRepository, times(1)).deleteById(1L);
    }
}
