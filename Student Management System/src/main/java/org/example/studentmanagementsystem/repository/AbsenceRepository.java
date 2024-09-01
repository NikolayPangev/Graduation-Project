package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Absence;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByStudentAndSubject(Student student, Subject subject);

    List<Absence> findByStudent(Student student);
}
