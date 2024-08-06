package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private List<Grade> grades;

    @OneToMany(mappedBy = "student")
    private Set<Absence> absences;

    @ManyToMany(mappedBy = "students", fetch = FetchType.EAGER)
    private List<Teacher> teachers;

    public Double getAverageGrade() {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        double sum = 0.0;
        for (Grade grade : grades) {
            sum += grade.getGrade();
        }
        return sum / grades.size();
    }

}
