package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private List<Teacher> teacher;

    @OneToMany(mappedBy = "subject")
    private Set<Grade> grades;
}
