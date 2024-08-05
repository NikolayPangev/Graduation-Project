package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    List<Student> findByClasses_ClassIdOrderByFirstNameAscLastNameAscMiddleNameAsc(Long classId);

    @Query("SELECT s FROM Student s JOIN FETCH s.classes WHERE s.username = :username")
    Student findByUsernameWithClass(@Param("username") String username);
}
