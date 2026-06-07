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

    @MockBean(name = "customJwtRequestFilter")
    private JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "customUserDetailsService")
    private UserDetailsServiceImpl userDetailsService;

    @MockBean(name = "customJwtAuthenticationEntryPoint")
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean(name = "passwordEncoder")
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        // 🛡️ Limpiar el contexto para asegurar que cada test es independiente y anónimo
        SecurityContextHolder.clearContext();

        // 🛡️ Configurar el filtro mock como un "pass-through" limpio
        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0, HttpServletRequest.class);
            HttpServletResponse response = invocation.getArgument(1, HttpServletResponse.class);
            FilterChain chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(request, response);
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
        // 🛡️ PROGRAMAR EL MOCK: Forzamos al EntryPoint a emitir un error 401 real
        // 🛡️ PROGRAMAR EL MOCK: Forzamos al EntryPoint a establecer el status 401 y comprometer la respuesta
        Mockito.doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1, HttpServletResponse.class);

            // sendError es el mecanismo que MockMvc captura de forma robusta para devolver 401
            // y detener el flujo de la cadena de filtros antes de llegar al controlador.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            HttpServletResponse responseq = invocation.getArgument(1, HttpServletResponse.class);
            // Establecemos el status explícitamente
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Escribimos algo en el cuerpo y hacemos flush para "comprometer" la respuesta
            response.getWriter().write("Unauthorized Access Required");
            response.getWriter().flush();
            return null;
        }).when(jwtAuthenticationEntryPoint).commence(any(), any(), any());
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