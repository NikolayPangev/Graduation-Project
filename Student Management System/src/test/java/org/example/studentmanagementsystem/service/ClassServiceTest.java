package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.repository.ClassRepository;
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
class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private ClassService classService;

    private Class clazz;
    private List<Class> classes;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        clazz = new Class();
        clazz.setClassId(1L);
        clazz.setGrade(5);
        clazz.setSection('A');

        teacher = new Teacher();
        teacher.setUserId(1L);
        teacher.setUsername("testteacher");

        classes = Arrays.asList(clazz);
    }

    @Test
    void createClass_ValidClass_ClassCreated() {
        when(classRepository.findByGradeAndSection(5, 'A')).thenReturn(Optional.empty());
        classService.createClass(clazz);
        verify(classRepository, times(1)).save(clazz);
    }

    @Test
    void createClass_InvalidGrade_ThrowsException() {
        clazz.setGrade(0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> classService.createClass(clazz));
        assertEquals("Grade must be between 1 and 12", exception.getMessage());
    }

    @Test
    void createClass_InvalidSection_ThrowsException() {
        clazz.setSection('E');
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> classService.createClass(clazz));
        assertEquals("Section must be A, B, C, or D", exception.getMessage());
    }

    @Test
    void createClass_ClassAlreadyExists_ThrowsException() {
        when(classRepository.findByGradeAndSection(5, 'A')).thenReturn(Optional.of(clazz));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> classService.createClass(clazz));
        assertEquals("This class already exists", exception.getMessage());
    }

    @Test
    void findByIds_ClassesExist() {
        when(classRepository.findAllById(Arrays.asList(1L))).thenReturn(classes);
        List<Class> foundClasses = classService.findByIds(Arrays.asList(1L));
        assertNotNull(foundClasses);
        assertEquals(1, foundClasses.size());
    }

    @Test
    void findById_ClassExists() {
        when(classRepository.findById(1L)).thenReturn(Optional.of(clazz));
        Optional<Class> foundClass = classService.findById(1L);
        assertTrue(foundClass.isPresent());
        assertEquals(clazz.getClassId(), foundClass.get().getClassId());
    }

    @Test
    void findById_ClassDoesNotExist() {
        when(classRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Class> foundClass = classService.findById(1L);
        assertFalse(foundClass.isPresent());
    }

    @Test
    void findAllClasses_ClassesExist() {
        when(classRepository.findAll()).thenReturn(classes);
        List<Class> foundClasses = classService.findAllClasses();
        assertNotNull(foundClasses);
        assertEquals(1, foundClasses.size());
    }

    @Test
    void save_ClassSaved() {
        classService.save(clazz);
        verify(classRepository, times(1)).save(clazz);
    }

    @Test
    void deleteClass_ClassDeleted() {
        classService.deleteClass(1L);
        verify(classRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllClassesOrderedByGradeAndSection_ClassesExist() {
        when(classRepository.findAllByOrderByGradeAscSectionAsc()).thenReturn(classes);
        List<Class> foundClasses = classService.getAllClassesOrderedByGradeAndSection();
        assertNotNull(foundClasses);
        assertEquals(1, foundClasses.size());
    }

    @Test
    void findClassesByTeacher_ClassesExist() {
        when(classRepository.findByTeachersContaining(teacher)).thenReturn(classes);
        List<Class> foundClasses = classService.findClassesByTeacher(teacher);
        assertNotNull(foundClasses);
        assertEquals(1, foundClasses.size());
    }
}
