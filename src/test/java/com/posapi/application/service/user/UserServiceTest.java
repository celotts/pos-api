package com.posapi.application.service.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService; // Instancia real bajo prueba
    private User user1;

    @BeforeEach
    void setUp() {
        // 🔥 Inicialización manual explícita (Cero problemas de inyección)
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);

        user1 = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .fullName("Test User")
                .isActive(true)
                .role("USER")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 🔥 Rellenamos todos los campos @NonNull exigidos por Lombok
        User newUser = User.builder()
                .id(UUID.randomUUID()) // Se lo mandamos simulando la pre-asignación
                .email("new@example.com")
                .passwordHash("rawPassword")
                .fullName("New User")
                .role("") // Lo mandamos vacío para probar que el servicio lo cambie a "USER"
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
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
                .role("ADMIN")
                .isActive(false)
                .createdAt(Instant.now()) // 🔥 Obligatorio por Lombok @NonNull
                .updatedAt(Instant.now()) // 🔥 Obligatorio por Lombok @NonNull
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser);
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