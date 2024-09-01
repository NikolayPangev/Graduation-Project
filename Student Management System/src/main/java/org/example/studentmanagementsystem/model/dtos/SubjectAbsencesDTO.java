package org.example.studentmanagementsystem.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.studentmanagementsystem.model.entities.Absence;
import org.example.studentmanagementsystem.model.entities.Subject;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubjectAbsencesDTO {
    private Subject subject;
    private List<Absence> absences;
    private int totalAbsences;
}
