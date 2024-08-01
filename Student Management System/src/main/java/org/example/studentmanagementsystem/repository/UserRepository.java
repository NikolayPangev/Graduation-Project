package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByRole(UserRole userRole);
}