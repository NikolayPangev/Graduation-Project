package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
