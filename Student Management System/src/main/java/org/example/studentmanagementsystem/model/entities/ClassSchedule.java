package org.example.studentmanagementsystem.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "class_schedules")
@Getter
@Setter
@NoArgsConstructor
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The class this schedule row belongs to (for class timetables)
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class schoolClass;

    // The teacher this schedule row belongs to (for teacher timetables)
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // If this is a class timetable: which subject is scheduled in the slot (nullable)
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // If this is a teacher timetable: which class is scheduled in the slot (nullable)
    @ManyToOne
    @JoinColumn(name = "assigned_class_id")
    private Class assignedClass;

    // Day and time range define the slot
    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // optional room
    private String room;

    // convenience ctor
    public ClassSchedule(Class schoolClass, DayOfWeek dayOfWeek, LocalTime start, LocalTime end) {
        this.schoolClass = schoolClass;
        this.dayOfWeek = dayOfWeek;
        this.startTime = start;
        this.endTime = end;
    }

    public ClassSchedule(Teacher teacher, DayOfWeek dayOfWeek, LocalTime start, LocalTime end, boolean forTeacher) {
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.startTime = start;
        this.endTime = end;
    }
}
