package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);

    List<Student> findByClasses_ClassIdOrderByFirstNameAscLastNameAscMiddleNameAsc(Long classId);

    @Query("SELECT s FROM Student s JOIN s.classes c JOIN c.teachers t WHERE t = :teacher ORDER BY c.grade, c.section, s.firstName, s.lastName, s.middleName")
    List<Student> findStudentsByTeacherOrdered(Teacher teacher);

    List<Student> findByClasses(Class cls);

    List<Student> findAllByClassesClassId(Long classId);
}
