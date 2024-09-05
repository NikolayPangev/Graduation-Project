package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.dtos.SubjectAbsencesDTO;
import org.example.studentmanagementsystem.model.entities.Absence;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.repository.AbsenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AbsenceService {

    private final AbsenceRepository absenceRepository;

    public AbsenceService(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    public Absence save(Absence absence) {
        return absenceRepository.save(absence);
    }

    public List<Absence> findByStudentAndSubject(Student student, Subject subject) {
        return absenceRepository.findByStudentAndSubject(student, subject);
    }

    public List<SubjectAbsencesDTO> findAbsencesByStudent(Student child) {
        List<Absence> absences = absenceRepository.findByStudent(child);

        Map<Subject, List<Absence>> absencesBySubject = absences.stream()
                .collect(Collectors.groupingBy(Absence::getSubject));

        return absencesBySubject.entrySet().stream()
                .map(entry -> new SubjectAbsencesDTO(
                        entry.getKey(),
                        entry.getValue(),
                        entry.getValue().size()
                ))
                .collect(Collectors.toList());
    }
}