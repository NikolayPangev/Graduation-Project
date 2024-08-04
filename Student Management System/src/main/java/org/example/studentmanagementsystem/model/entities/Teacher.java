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
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher")
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "teacher")
    private Set<Class> classes;

}
