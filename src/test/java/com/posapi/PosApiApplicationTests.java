package com.posapi;

import com.posapi.domain.repository.user.UserRepository;
import com.posapi.infrastructure.adapter.output.persistence.user.UserRepositoryAdapter;
import com.posapi.infrastructure.security.JwtAuthenticationEntryPoint;
import com.posapi.infrastructure.security.JwtRequestFilter;
import com.posapi.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Excluimos el adaptador para que no intente conectar a la base de datos real
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        UserRepositoryAdapter.class }))
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000"
})
@ActiveProfiles("test")
class PosApiApplicationTests {

    // --- MOCKS NECESARIOS ---

    @MockitoBean
    private UserRepository userRepository;



    @MockitoBean(name = "customJwtRequestFilter")
    private JwtRequestFilter jwtRequestFilter;

    @MockitoBean(name = "customJwtAuthenticationEntryPoint")
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean(name = "customUserDetailsService")
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        // El test pasará porque ya no falta ningún bean en el contexto
    }
}