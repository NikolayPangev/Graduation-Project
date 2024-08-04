package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class classes;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy = "student")
    private Set<Grade> grades;

    @OneToMany(mappedBy = "student")
    private Set<Absence> absences;

}
