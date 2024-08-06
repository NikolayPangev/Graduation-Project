package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    private Subject subject;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Class> classes;

    @ManyToMany
    @JoinTable(
            name = "teacher_student",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

}
