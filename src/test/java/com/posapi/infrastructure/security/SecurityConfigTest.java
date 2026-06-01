package com.posapi.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(SecurityConfigTest.TestController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000"
})
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtRequestFilter jwtRequestFilter; // Mantenemos el mock

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/auth/public")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testProtectedEndpoint() throws Exception {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        mockMvc.perform(get("/users/protected")
                        .with(user(userDetails)) // Aquí Spring Test inyecta el usuario directo al contexto
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testProtectedEndpointUnauthenticated() throws Exception {
        // 🔥 Configurar el mock para que actúe como un filtro neutral que solo deja pasar la petición
        // sin autenticar a nadie, forzando a que HttpSecurity tome el control y lo rebote.
        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtRequestFilter).doFilter(any(), any(), any());

        mockMvc.perform(get("/users/protected")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 🛡️ Al pasar sin usuario asignado, el HttpSecurity real arrojará 403 Forbidden
    }

    @RestController
    static class TestController {
        @GetMapping("/auth/public")
        public String publicEndpoint() {
            return "public";
        }

        @GetMapping("/users/protected")
        public String protectedEndpoint() {
            return "protected";
        }
    }
}