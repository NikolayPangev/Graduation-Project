package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    private List<Subject> subjects;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    private List<Class> classes;

}
