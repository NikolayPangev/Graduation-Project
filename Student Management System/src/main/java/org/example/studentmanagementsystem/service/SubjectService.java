package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.SubjectRepository; // Add a repository for subjects
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public boolean existsByName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName).isPresent();
    }

    public void createSubject(String subjectName) {
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subjectRepository.save(subject);
    }

    public List<Subject> findAllSubjects() {
        return subjectRepository.findAll();
    }

    public void deleteSubject(Long subjectId) {
        subjectRepository.deleteById(subjectId);
    }
}
