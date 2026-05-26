package com.posapi.application.service.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("encodedPassword") // Ya codificada para el mock
                .fullName("Test User")
                .isActive(true)
                .role("USER")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        // Mockear el comportamiento del PasswordEncoder
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Mockear el comportamiento del UserRepository para devolver el mismo objeto User que recibió
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User newUser = User.builder()
                .email("new@example.com")
                .passwordHash("rawPassword") // Contraseña sin codificar
                .fullName("New User")
                .build();

        User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser.getId()); // El ID debería estar seteado por el servicio
        assertEquals("encodedPassword", createdUser.getPasswordHash());
        assertEquals("USER", createdUser.getRole());
        assertTrue(createdUser.getIsActive());
        assertNotNull(createdUser.getCreatedAt());
        assertNotNull(createdUser.getUpdatedAt());

        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldUseProvidedIdAndRole() {
        UUID providedId = UUID.randomUUID();
        User newUser = User.builder()
                .id(providedId)
                .email("provided@example.com")
                .passwordHash("rawPassword")
                .fullName("Provided User")
                .role("ADMIN") // Rol proporcionado
                .isActive(false) // Estado activo proporcionado
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        // Mockear el comportamiento del UserRepository para devolver el mismo objeto User que recibió
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(newUser);

        assertEquals(providedId, createdUser.getId());
        assertEquals("ADMIN", createdUser.getRole());
        assertFalse(createdUser.getIsActive());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUser_whenFound() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        Optional<User> foundUser = userService.getUserById(user1.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user1.getEmail(), foundUser.get().getEmail());
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    void getUserById_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(UUID.randomUUID());

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getUserByEmail_shouldReturnUser_whenFound() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        Optional<User> foundUser = userService.getUserByEmail(user1.getEmail());

        assertTrue(foundUser.isPresent());
        assertEquals(user1.getFullName(), foundUser.get().getFullName());
        verify(userRepository, times(1)).findByEmail(user1.getEmail());
    }

    @Test
    void getUserByEmail_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}
