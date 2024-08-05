package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAllTeachers() {
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Teacher> findByClassId(Long classId) {
        return teacherRepository.findByClasses_ClassId(classId);
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> findByClassAndSubject(Long classId, Long subjectId) {
        return teacherRepository.findByClasses_ClassIdAndSubjects_SubjectId(classId, subjectId);
    }

    public void save(Teacher currentTeacher) {
        teacherRepository.save(currentTeacher);
    }

}
