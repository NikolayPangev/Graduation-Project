package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
}
