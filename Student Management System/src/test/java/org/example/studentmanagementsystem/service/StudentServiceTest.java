package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private List<Student> students;
    private Teacher teacher;
    private Class cls;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setUserId(1L);
        student.setUsername("teststudent");

        students = Arrays.asList(student);

        teacher = new Teacher();
        teacher.setUserId(1L);
        teacher.setUsername("testteacher");

        cls = new Class();
        cls.setClassId(1L);
        cls.setGrade(10);
        cls.setSection('A');
        cls.setStudents(Set.of(student));
        cls.setTeachers(Set.of(teacher));
    }

    @Test
    void findAllStudents_StudentsExist() {
        when(studentRepository.findAll()).thenReturn(students);
        List<Student> foundStudents = studentService.findAllStudents();
        assertNotNull(foundStudents);
        assertEquals(1, foundStudents.size());
    }

    @Test
    void findById_StudentExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Optional<Student> foundStudent = studentService.findById(1L);
        assertTrue(foundStudent.isPresent());
        assertEquals(student.getUserId(), foundStudent.get().getUserId());
    }

    @Test
    void findById_StudentDoesNotExist() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Student> foundStudent = studentService.findById(1L);
        assertFalse(foundStudent.isPresent());
    }

    @Test
    void save_StudentSaved() {
        studentService.save(student);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void deleteStudent_StudentDeleted() {
        studentService.deleteStudent(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByIds_StudentsExist() {
        when(studentRepository.findAllById(Arrays.asList(1L))).thenReturn(students);
        List<Student> foundStudents = studentService.findByIds(Arrays.asList(1L));
        assertNotNull(foundStudents);
        assertEquals(1, foundStudents.size());
    }

    @Test
    void findByUsername_StudentExists() {
        when(studentRepository.findByUsername("teststudent")).thenReturn(Optional.of(student));
        Optional<Student> foundStudent = studentService.findByUsername("teststudent");
        assertTrue(foundStudent.isPresent());
        assertEquals(student.getUsername(), foundStudent.get().getUsername());
    }

    @Test
    void findAllByClassId_StudentsExist() {
        when(studentRepository.findByClasses_ClassIdOrderByFirstNameAscLastNameAscMiddleNameAsc(1L)).thenReturn(students);
        List<Student> foundStudents = studentService.findAllByClassId(1L);
        assertNotNull(foundStudents);
        assertEquals(1, foundStudents.size());
    }

    @Test
    void findStudentsByTeacherOrdered_StudentsExist() {
        when(studentRepository.findStudentsByTeacherOrdered(teacher)).thenReturn(students);
        List<Student> foundStudents = studentService.findStudentsByTeacherOrdered(teacher);
        assertNotNull(foundStudents);
        assertEquals(1, foundStudents.size());
    }

    @Test
    void findStudentsByClass_StudentsExist() {
        when(studentRepository.findByClasses(cls)).thenReturn(students);
        List<Student> foundStudents = studentService.findStudentsByClass(cls);
        assertNotNull(foundStudents);
        assertEquals(1, foundStudents.size());
    }
}