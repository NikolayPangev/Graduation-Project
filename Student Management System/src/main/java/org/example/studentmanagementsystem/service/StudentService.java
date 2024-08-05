package org.example.studentmanagementsystem.service;

import jakarta.transaction.Transactional;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> findById(Long studentId) {
        return studentRepository.findById(studentId);
    }

    @Transactional
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }

    public List<Student> findByIds(List<Long> studentIds) {
        return studentRepository.findAllById(studentIds);
    }

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public List<Student> findAllByClassId(Long classId) {
        return studentRepository.findByClasses_ClassIdOrderByFirstNameAscLastNameAscMiddleNameAsc(classId);
    }

    public List<Student> findAllStudentsByTeacher(Teacher teacher) {
        return studentRepository.findByTeachersContaining(teacher);
    }
}
