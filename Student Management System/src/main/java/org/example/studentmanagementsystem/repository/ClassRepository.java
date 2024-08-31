package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByGradeAndSection(int grade, char section);

    List<Class> findAllByOrderByGradeAscSectionAsc();

    List<Class> findByTeachersContaining(Teacher teacher);

    @Query("SELECT c FROM Class c JOIN c.students s WHERE s.userId = :studentId")
    Optional<Class> findClassByStudentId(@Param("studentId") Long studentId);
}

