package com.posapi.infrastructure.adapter.input.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.dto.user.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.dto.user.UserResponse;
import com.posapi.infrastructure.adapter.input.rest.user.mapper.UserRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private UserManagementPort userManagementPort;


    private UserRestMapper userRestMapper;

    @Test
    @WithMockUser
    void registerUser_ShouldReturn201() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .roleId(UUID.randomUUID())
                .isActive(true)
                .build();

        User userTemplate = User.builder().email("test@example.com").build();
        User createdUser = User.builder().id(UUID.randomUUID()).email("test@example.com").fullName("Test User").build();
        UserResponse responseDto = UserResponse.builder().id(createdUser.getId()).email(createdUser.getEmail()).build();

        given(userRestMapper.toDomain(any(UserRequest.class))).willReturn(userTemplate);
        given(userManagementPort.createUser(any(User.class))).willReturn(createdUser);
        given(userRestMapper.toResponse(any(User.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdUser.getId().toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void getUserById_ShouldReturn200_WhenUserExists() throws Exception {
        UUID id = UUID.randomUUID();
        User foundUser = User.builder().id(id).build();
        UserResponse responseDto = UserResponse.builder().id(id).build();

        given(userManagementPort.getUserById(id)).willReturn(Optional.of(foundUser));
        given(userRestMapper.toResponse(foundUser)).willReturn(responseDto);

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
}
