package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public void save(Grade grade) {
        gradeRepository.save(grade);
    }

    public Optional<Grade> findById(Long gradeId) {
        return gradeRepository.findById(gradeId);
    }

    public void delete(Grade grade) {
        gradeRepository.delete(grade);
    }

    public List<Grade> findByStudentAndSubject(Student student, Subject subject) {
        return gradeRepository.findByStudentAndSubject(student, subject);
    }
}
