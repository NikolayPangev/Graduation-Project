package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    @Column(name = "grade", nullable = false)
    private int grade;

    @Column(name = "section", nullable = false)
    private char section;

    @OneToMany(mappedBy = "classes", fetch = FetchType.EAGER)
    private Set<Student> students;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Teacher> teachers;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
