package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.Parent;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.repository.ParentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;

    @InjectMocks
    private ParentService parentService;

    private Parent parent;
    private List<Parent> parents;
    private Student student;

    @BeforeEach
    void setUp() {
        parent = new Parent();
        parent.setUserId(1L);
        parent.setUsername("testparent");

        parents = Arrays.asList(parent);

        student = new Student();
        student.setUserId(1L);
        student.setUsername("teststudent");
    }

    @Test
    void save_ParentSaved() {
        parentService.save(parent);
        verify(parentRepository, times(1)).save(parent);
    }

    @Test
    void deleteParent_ParentDeleted() {
        parentService.deleteParent(1L);
        verify(parentRepository, times(1)).deleteById(1L);
    }

    @Test
    void findAllParents_ParentsExist() {
        when(parentRepository.findAll()).thenReturn(parents);
        List<Parent> foundParents = parentService.findAllParents();
        assertNotNull(foundParents);
        assertEquals(1, foundParents.size());
    }

    @Test
    void findById_ParentExists() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        Optional<Parent> foundParent = parentService.findById(1L);
        assertTrue(foundParent.isPresent());
        assertEquals(parent.getUserId(), foundParent.get().getUserId());
    }

    @Test
    void findById_ParentDoesNotExist() {
        when(parentRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Parent> foundParent = parentService.findById(1L);
        assertFalse(foundParent.isPresent());
    }

    @Test
    void findParentsByStudent_ParentsExist() {
        when(parentRepository.findParentsByStudent(student)).thenReturn(parents);
        List<Parent> foundParents = parentService.findParentsByStudent(student);
        assertNotNull(foundParents);
        assertEquals(1, foundParents.size());
    }

    @Test
    void findByUsername_ParentExists() {
        when(parentRepository.findByUsername("testparent")).thenReturn(Optional.of(parent));
        Optional<Parent> foundParent = parentService.findByUsername("testparent");
        assertTrue(foundParent.isPresent());
        assertEquals(parent.getUsername(), foundParent.get().getUsername());
    }
}