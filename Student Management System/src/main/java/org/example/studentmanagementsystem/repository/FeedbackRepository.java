package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Feedback;
import org.example.studentmanagementsystem.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStudent(Student student);
}
