package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
}
