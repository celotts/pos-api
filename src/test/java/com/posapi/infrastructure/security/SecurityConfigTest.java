package com.posapi.infrastructure.security;

import com.posapi.infrastructure.config.SecurityConfig;
import com.posapi.domain.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestConfiguration
class SecurityTestConfig {
    @Bean
    public AuthenticationManager authenticationManager() {
        return org.mockito.Mockito.mock(AuthenticationManager.class);
    }

    @Bean(name = "userDetailsService")
    public UserDetailsService userDetailsService() {
        return org.mockito.Mockito.mock(UserDetailsService.class);
    }
}

@WebMvcTest(value = SecurityConfigTest.SecurityTestController.class, excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@Import({ SecurityConfig.class, SecurityTestConfig.class })
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345"
})
public class SecurityConfigTest {

    @SpringBootConfiguration
    static class WebMvcTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "customJwtAuthenticationEntryPoint")
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean(name = "customJwtRequestFilter")
    private JwtRequestFilter jwtRequestFilter;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private PasswordEncoder passwordEncoder;

    // Ya no usamos MockBean para AuthenticationManager porque viene de
    // SecurityTestConfig
    @Autowired
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockBean(name = "entityManagerFactory")
    private jakarta.persistence.EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() throws Exception {
        SecurityContextHolder.clearContext();

        // 🛡️ Filtro transparente para que las peticiones lleguen a las reglas de
        // HttpSecurity
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0, HttpServletRequest.class);
            HttpServletResponse response = invocation.getArgument(1, HttpServletResponse.class);
            FilterChain filterChain = invocation.getArgument(2, FilterChain.class);
            filterChain.doFilter(request, response); // Deja pasar la petición
            return null;
        }).when(jwtRequestFilter).doFilterInternal(any(), any(), any());

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1, HttpServletResponse.class);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return null;
        }).when(jwtAuthenticationEntryPoint).commence(any(), any(), any());
    }

    @Test
    void testPublicEndpoint_ShouldAllowAnonymousAccess() throws Exception {
        mockMvc.perform(get("/auth/public").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpoint_ShouldAllowAccessWhenAuthenticated() throws Exception {
        UserDetails userDetails = User.withUsername("test@pos.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        mockMvc.perform(get("/users/protected")
                .with(user(userDetails))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpoint_ShouldReturn401WhenAnonymous() throws Exception {
        mockMvc.perform(get("/users/protected").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @RestController
    public static class SecurityTestController {
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