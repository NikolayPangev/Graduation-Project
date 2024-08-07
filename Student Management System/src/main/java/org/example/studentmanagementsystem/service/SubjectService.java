package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName).isPresent();
    }

    @Transactional
    public void createSubject(String subjectName) {
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subjectRepository.save(subject);
    }

    @Transactional(readOnly = true)
    public List<Subject> findAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public Subject findByTeacherId(Long teacherId) {
        return subjectRepository.findByTeacher_UserId(teacherId);
    }

    @Transactional
    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long subjectId) {
        subjectRepository.deleteById(subjectId);
    }

}
