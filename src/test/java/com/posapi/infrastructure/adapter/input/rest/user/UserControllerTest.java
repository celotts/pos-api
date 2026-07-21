package com.posapi.infrastructure.adapter.input.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posapi.application.port.user.UserManagementPort;
import com.posapi.application.service.jwt.JwtService;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import com.posapi.infrastructure.adapter.input.rest.user.mapper.UserRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserManagementPort userManagementPort;

    @MockitoBean
    private UserRestMapper userRestMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @SuppressWarnings("null")
    @Test
    @WithMockUser
    void registerUserShouldReturn201() throws Exception {
        UserRequest request = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .roleId(UUID.randomUUID())
                .isActive(true)
                .build();

        UserResponse responseDto = UserResponse.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .build();

        given(userManagementPort.createUser(any(UserRequest.class)))
                .willReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void getUserByIdShouldReturn200WhenUserExists() throws Exception {
        UUID id = UUID.randomUUID();
        UserResponse responseDto = UserResponse.builder()
                .id(id)
                .email("found@example.com")
                .fullName("Found User")
                .build();

        given(userManagementPort.getUserById(id))
                .willReturn(Optional.of(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
}
