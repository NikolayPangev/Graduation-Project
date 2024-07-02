package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
