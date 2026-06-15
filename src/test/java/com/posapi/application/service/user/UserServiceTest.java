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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para UserService.
 * Se utiliza MockitoExtension para inyectar mocks y aislar la lógica de negocio
 * de la infraestructura (Persistencia y Seguridad).
 */
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

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@posapi.com");
        testUser.setPasswordHash("rawPassword");
        testUser.setRole("ADMIN");
        testUser.setIsActive(true);
    }

    @Test
    @DisplayName("Debe codificar la contraseña y asignar rol por defecto al crear usuario")
    void createUser_ShouldEncodePasswordAndSetDefaults() {
        // Arrange: Preparamos un usuario con rol vacío para probar la lógica de defaults
        testUser.setRole(""); 
        when(passwordEncoder.encode("rawPassword")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User created = userService.createUser(testUser);

        // Assert
        assertThat(created.getPasswordHash()).isEqualTo("encoded_pass");
        assertThat(created.getRole()).isEqualTo("USER"); // Validamos el default "USER"
        assertThat(created.getCreatedAt()).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Debe retornar todos los usuarios de la base de datos")
    void getAllUsers_ShouldReturnList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(1);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Debe encontrar un usuario por su ID")
    void getUserById_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@posapi.com");
    }

    @Test
    @DisplayName("Debe actualizar los datos y la contraseña si se proporciona")
    void updateUser_ShouldUpdateAllFields() {
        // Arrange
        User updatedInfo = new User();
        updatedInfo.setEmail("new@posapi.com");
        updatedInfo.setFullName("Updated Name");
        updatedInfo.setPasswordHash("newRawPassword");
        updatedInfo.setRole("MANAGER");
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
        assertThat(updated.getPasswordHash()).isEqualTo("new_encoded_pass");
        assertThat(updated.getRole()).isEqualTo("MANAGER");
        assertThat(updated.getIsActive()).isFalse();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar al usuario si existe")
    void deleteUser_ShouldReturnTrueWhenFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        boolean deleted = userService.deleteUser(userId);

        // Assert
        assertThat(deleted).isTrue();
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Debe retornar false al intentar eliminar un usuario inexistente")
    void deleteUser_ShouldReturnFalseWhenNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        boolean deleted = userService.deleteUser(userId);

        // Assert
        assertThat(deleted).isFalse();
        verify(userRepository, never()).delete(any());
    }
}