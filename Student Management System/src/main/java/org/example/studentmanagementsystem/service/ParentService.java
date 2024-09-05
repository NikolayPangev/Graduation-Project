package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Parent;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.repository.ParentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Transactional
    public void save(Parent parent) {
        parentRepository.save(parent);
    }

    @Transactional
    public void deleteParent(Long parentId) {
        parentRepository.deleteById(parentId);
    }

    public List<Parent> findAllParents() {
        return parentRepository.findAll();
    }

    public Optional<Parent> findById(Long parentId) {
        return parentRepository.findById(parentId);
    }

    public List<Parent> findParentsByStudent(Student student) {
        return parentRepository.findParentsByStudent(student);
    }

    public Optional<Parent> findByUsername(String username) {
        return parentRepository.findByUsername(username);
    }

    public long countParents() {
        return parentRepository.count();
    }
}

