package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
}
