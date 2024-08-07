package org.example.studentmanagementsystem.config;

import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class DataInitializerTest {

    @Test
    void testCommandLineRunnerInitializesAdmin() {
        UserService userService = mock(UserService.class);
        DataInitializer dataInitializer = new DataInitializer(userService);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.adminExists()).thenReturn(false);

        CommandLineRunner commandLineRunner = dataInitializer.init();
        assertDoesNotThrow(() -> commandLineRunner.run());

        verify(userService, times(1)).createAdmin(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertTrue(capturedUser.getUsername().equals("Admin"));
        assertTrue(capturedUser.getFirstName().equals("Admin"));
        assertTrue(capturedUser.getLastName().equals("Admin"));
        assertTrue(capturedUser.getEmail().equals("admin@admin.com"));
        assertTrue(capturedUser.getPassword().equals("Admin123"));

        verify(userService, times(1)).adminExists();
    }
}
