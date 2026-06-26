package com.posapi.application.service.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    // IDs constantes para las pruebas
    private static final UUID ADMIN_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USER_ROLE_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@posapi.com");
        testUser.setPassword("rawPassword");
        testUser.setRoleId(USER_ROLE_ID);
        testUser.setIsActive(true);
    }

    @Test
    @DisplayName("Debe codificar la contraseña y asignar rol por defecto al crear usuario")
    void createUser_ShouldEncodePasswordAndSetDefaults() {
        // Arrange
        testUser.setRoleId(null); // Simulamos que llega sin rol para activar el default
        when(passwordEncoder.encode("rawPassword")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setCreatedAt(Instant.now());
            return u;
        });

        // Act
        User created = userService.createUser(testUser);

        // Assert
        assertThat(created.getPassword()).isEqualTo("encoded_pass");
        assertThat(created.getRoleId()).isEqualTo(USER_ROLE_ID); // Validamos que se asignó el ID del rol USER
        assertThat(created.getCreatedAt()).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Debe actualizar los datos y la contraseña si se proporciona")
    void updateUser_ShouldUpdateAllFields() {
        // Arrange
        User updatedInfo = new User();
        updatedInfo.setEmail("new@posapi.com");
        updatedInfo.setFullName("Updated Name");
        updatedInfo.setPassword("newRawPassword");
        updatedInfo.setRoleId(ADMIN_ROLE_ID); // Usamos ID en lugar de nombre
        updatedInfo.setIsActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newRawPassword")).thenReturn("new_encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<User> result = userService.updateUser(userId, updatedInfo);

        // Assert
        assertThat(result).isPresent();
        User updated = result.get();
        assertThat(updated.getEmail()).isEqualTo("new@posapi.com");
        assertThat(updated.getPassword()).isEqualTo("new_encoded_pass");
        assertThat(updated.getRoleId()).isEqualTo(ADMIN_ROLE_ID);
        assertThat(updated.getIsActive()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    // ... mantén tus otros tests (getAll, getById, delete) tal como están.
}