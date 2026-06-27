package com.posapi.application.service.user;

import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoleRepository roleRepository;
    @InjectMocks private UserService userService;

    private User testUser;
    private UUID userId;

    private static final UUID USER_ROLE_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final Role USER_ROLE = new Role(USER_ROLE_ID, "USER");

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .email("test@posapi.com")
                .password("encodedPassword")
                .fullName("Test User")
                .roleId(USER_ROLE_ID)
                .isActive(true)
                .failedLoginAttempts(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Debe codificar la contraseña y asignar rol por defecto al crear usuario")
    void createUser_ShouldEncodePasswordAndSetDefaults() {
        // Arrange
        User newUserRequest = User.builder()
                .email("new@posapi.com")
                .password("rawPassword")
                .fullName("New User")
                .build();

        when(userRepository.findByEmail("new@posapi.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(roleRepository.findByName(USER_ROLE.getName())).thenReturn(Optional.of(USER_ROLE));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User created = userService.createUser(newUserRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(created).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("new@posapi.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRoleId()).isEqualTo(USER_ROLE_ID);
        assertThat(savedUser.getIsActive()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe lanzar una excepción si el email ya existe al crear")
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        User existingUser = User.builder().email("test@posapi.com").build();
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(existingUser);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar los datos y la contraseña si se proporciona")
    void updateUser_ShouldUpdateAllFields() {
        // Arrange
        UUID managerRoleId = UUID.randomUUID();
        Role managerRole = new Role(managerRoleId, "MANAGER");

        User updatedInfo = User.builder()
                .email("new@posapi.com")
                .fullName("Updated Name")
                .password("newRawPassword")
                .roleId(managerRoleId)
                .isActive(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newRawPassword")).thenReturn("new_encoded_pass");
        when(roleRepository.findById(managerRoleId)).thenReturn(Optional.of(managerRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<User> result = userService.updateUser(userId, updatedInfo);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(result).isPresent();
        assertThat(savedUser.getId()).isEqualTo(userId);
        assertThat(savedUser.getEmail()).isEqualTo("new@posapi.com");
        assertThat(savedUser.getPassword()).isEqualTo("new_encoded_pass");
        assertThat(savedUser.getRoleId()).isEqualTo(managerRoleId);
        assertThat(savedUser.getIsActive()).isFalse();
        assertThat(savedUser.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
        assertThat(savedUser.getUpdatedAt()).isAfter(testUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe eliminar un usuario existente")
    void deleteUser_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).deleteById(userId);
    }
}
