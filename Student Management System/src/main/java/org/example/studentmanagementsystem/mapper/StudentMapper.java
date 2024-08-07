package org.example.studentmanagementsystem.mapper;

import org.example.studentmanagementsystem.model.dtos.StudentDTO;
import org.example.studentmanagementsystem.model.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    StudentDTO toStudentDTO(Student student);
}

