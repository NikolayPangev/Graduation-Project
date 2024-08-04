package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Parent;
import org.example.studentmanagementsystem.repository.ParentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public Optional<Parent> findById(Long id) {
        return parentRepository.findById(id);
    }
}
