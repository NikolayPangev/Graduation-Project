package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByClasses_ClassId(Long classId);

    Optional<Teacher> findByClasses_ClassIdAndSubject_SubjectId(Long classId, Long subjectId);
}
