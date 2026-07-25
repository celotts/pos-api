package com.posapi.application.service.user;

import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.PasswordEncoderPort;
import com.posapi.domain.port.output.RoleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import com.posapi.infrastructure.adapter.input.rest.user.mapper.UserRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SecurityContextHelper securityContextHelper;
    @Mock
    private UserRestMapper userRestMapper;

    @InjectMocks
    private UserService userService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID USER_ROLE_ID = UUID.randomUUID();
    private static final UUID ADMIN_ROLE_ID = UUID.randomUUID();

    private User user1;
    private Role userRole;
    private UserRequest userRequest1;
    private UserResponse userResponse1;

    @BeforeEach
    void setUp() {
        userRole = Role.builder()
                .id(USER_ROLE_ID)
                .name("USER")
                .build();

        user1 = User.builder()
                .id(UUID.randomUUID())
                .email("user1@example.com")
                .fullName("User One")
                .address("123 Main St") // AÑADIDO
                .phone("1234567890")   // AÑADIDO
                .phone2("0987654321")  // AÑADIDO
                .role(userRole)
                .createdByUserId(USER_ID)
                .createdAt(Instant.now())
                .isActive(true) // AÑADIDO
                .build();

        // CORREGIDO: Constructor de UserRequest
        userRequest1 = new UserRequest(
                user1.getEmail(),
                "password123",
                user1.getFullName(),
                user1.getAddress(), // AÑADIDO
                user1.getPhone(),  // AÑADIDO
                user1.getPhone2(), // AÑADIDO
                USER_ROLE_ID,
                user1.getIsActive() // isActive
        );

        // CORREGIDO: Constructor de UserResponse
        userResponse1 = new UserResponse(
                user1.getId(),
                user1.getEmail(),
                user1.getFullName(),
                "USER",
                user1.getAddress(), // AÑADIDO
                user1.getPhone(),  // AÑADIDO
                user1.getPhone2(), // AÑADIDO
                user1.getIsActive(), // isActive
                user1.getCreatedAt(),
                null // updatedAt
        );
    }

    @Test
    @DisplayName("Create user - Should return user response when user does not exist")
    void createUserShouldReturnUserResponseWhenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(securityContextHelper.getCurrentUserId()).thenReturn(USER_ID);
        when(userRepository.save(any(User.class))).thenReturn(user1);

        lenient().when(userRestMapper.toResponse(any(User.class), any(), any(), any())).thenReturn(userResponse1);

        UserResponse response = userService.createUser(userRequest1);

        assertNotNull(response);
        assertEquals(user1.getEmail(), response.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Create user - Should throw DuplicateResourceException when user already exists")
    void createUserShouldThrowDuplicateResourceExceptionWhenUserAlreadyExists() {
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        assertThrows(DuplicateResourceException.class, () ->
                userService.createUser(userRequest1)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Get user by ID - Should return user response when user exists")
    void getUserByIdShouldReturnUserResponseWhenUserExists() {
        UUID id = user1.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        when(userRestMapper.toResponse(eq(user1), any(), any(), any())).thenReturn(userResponse1);

        Optional<UserResponse> response = userService.getUserById(id);

        assertTrue(response.isPresent());
        assertEquals(user1.getEmail(), response.get().email());
    }

    @Test
    @DisplayName("Get user by ID - Should return empty optional when user does not exist")
    void getUserByIdShouldReturnEmptyOptionalWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserResponse> response = userService.getUserById(id);

        assertFalse(response.isPresent());
    }

    @Test
    @DisplayName("Get all users - Should return list of user responses")
    void getAllUsersShouldReturnListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(List.of(user1));
        when(userRestMapper.toResponse(eq(user1), any(), any(), any())).thenReturn(userResponse1);

        List<UserResponse> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(user1.getEmail(), users.getFirst().email());
    }

    @Test
    @DisplayName("Update user - Should return updated user response when user exists")
    void updateUserShouldReturnUpdatedUserResponseWhenUserExists() {
        UUID id = user1.getId();
        // CORREGIDO: Constructor de UserRequest
        UserRequest updateRequest = new UserRequest(
                "user1@example.com",
                "newpassword",
                "Updated Name",
                "456 Oak Ave", // AÑADIDO
                "9876543210",  // AÑADIDO
                "1122334455",  // AÑADIDO
                USER_ROLE_ID,
                true
        );
        User updatedUser = user1.toBuilder()
                .fullName("Updated Name")
                .address("456 Oak Ave") // AÑADIDO
                .phone("9876543210")   // AÑADIDO
                .phone2("1122334455")  // AÑADIDO
                .build();

        when(securityContextHelper.getCurrentUserId()).thenReturn(USER_ID);
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userRestMapper.toResponse(eq(updatedUser), any(), any(), any())).thenReturn(userResponse1);

        Optional<UserResponse> response = userService.updateUser(id, updateRequest);

        assertTrue(response.isPresent());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Update user - Should return empty optional when user does not exist")
    void updateUserShouldReturnEmptyOptionalWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserResponse> response = userService.updateUser(id, userRequest1);

        assertFalse(response.isPresent());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Delete user - Should mark user as deleted when user exists")
    void deleteUserShouldMarkUserAsDeletedWhenUserExists() {
        UUID id = user1.getId();
        when(securityContextHelper.getCurrentUserId()).thenReturn(USER_ID);
        when(securityContextHelper.getCurrentUserRoleId()).thenReturn(ADMIN_ROLE_ID);
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        userService.deleteUser(id);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Delete user - Should do nothing when user does not exist")
    void deleteUserShouldDoNothingWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        userService.deleteUser(id);

        verify(userRepository, never()).save(any(User.class));
    }
}
