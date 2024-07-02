package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
