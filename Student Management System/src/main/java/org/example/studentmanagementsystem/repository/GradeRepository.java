package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentAndSubject(Student student, Subject subject);
}
