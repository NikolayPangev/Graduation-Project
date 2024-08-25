package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    private Subject subject;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Class> classes;

    @ManyToMany
    @JoinTable(
            name = "teacher_student",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return getUserId() != null && getUserId().equals(teacher.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }

}
