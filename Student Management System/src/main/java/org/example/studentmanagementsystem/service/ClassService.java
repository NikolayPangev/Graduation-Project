package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.repository.ClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public void createClass(Class newClass) {
        if (newClass.getGrade() < 1 || newClass.getGrade() > 12) {
            throw new IllegalArgumentException("Grade must be between 1 and 12");
        }
        if ("ABCD".indexOf(newClass.getSection()) == -1) {
            throw new IllegalArgumentException("Section must be A, B, C, or D");
        }
        if (classRepository.findByGradeAndSection(newClass.getGrade(), newClass.getSection()).isPresent()) {
            throw new IllegalArgumentException("This class already exists");
        }
        classRepository.save(newClass);
    }

    public List<Class> getAllClassesOrderedByGradeAndSection() {
        return classRepository.findAllByOrderByGradeAscSectionAsc();
    }

    public void deleteClass(Long classId) {
        classRepository.deleteById(classId);
    }
}

