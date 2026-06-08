package com.posapi.infrastructure.security;

import com.posapi.infrastructure.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfigTest.TestController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000"
})
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "jwtUtil")
    private JwtUtil jwtUtil;

    @MockBean(name = "customUserDetailsService") // 🛡️ Debe coincidir con el @Service o @Qualifier de tu implementación real
    private UserDetailsService userDetailsService; 

    @MockBean(name = "customJwtAuthenticationEntryPoint") 
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockBean(name = "customJwtRequestFilter") 
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() throws Exception {
        // 🛡️ Limpiar el contexto para evitar interferencias entre ejecuciones
        SecurityContextHolder.clearContext();

        // 🛡️ Configurar el filtro mock como pass-through (deja pasar la petición)
        Mockito.doAnswer(invocation -> {
            HttpServletRequest req = invocation.getArgument(0);
            HttpServletResponse res = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            
            // No establecemos autenticación, solo permitimos que la cadena continúe
            chain.doFilter(req, res);
            return null;
        }).when(jwtRequestFilter).doFilterInternal(any(), any(), any());
    }

    @Test
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/auth/public")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointUnauthenticated() throws Exception {
        // 🛡️ Asegurar que no hay autenticación previa
        SecurityContextHolder.clearContext();

        // 🛡️ PROGRAMAR EL MOCK: Forzar al EntryPoint a emitir un error 401 real
        Mockito.doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            // Usamos setStatus y flush para obligar a MockMvc a reconocer la interrupción de seguridad
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized Access");
            response.getWriter().flush(); 
            return null;
        }).when(jwtAuthenticationEntryPoint).commence(any(), any(), any());

        // 3. 🚀 EJECUTAR Y VALIDAR: Realizamos la petición sin token
        mockMvc.perform(get("/users/protected")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
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