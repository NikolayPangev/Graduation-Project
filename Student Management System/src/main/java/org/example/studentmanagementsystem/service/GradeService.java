package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    private final SubjectService subjectService;

    private final StudentService studentService;

    public GradeService(GradeRepository gradeRepository, SubjectService subjectService, StudentService studentService) {
        this.gradeRepository = gradeRepository;
        this.subjectService = subjectService;
        this.studentService = studentService;
    }

    public void save(Grade grade) {
        gradeRepository.save(grade);
    }
}
