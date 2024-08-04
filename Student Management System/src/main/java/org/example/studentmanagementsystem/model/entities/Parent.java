package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Parent extends User {

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private List<Student> children = new ArrayList<>();

}
