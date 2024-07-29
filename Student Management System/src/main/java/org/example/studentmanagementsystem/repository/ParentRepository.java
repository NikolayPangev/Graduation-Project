package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
}
