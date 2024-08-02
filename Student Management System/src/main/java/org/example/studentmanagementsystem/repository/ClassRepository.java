package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByGradeAndSection(int grade, char section);
}

