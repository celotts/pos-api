package com.posapi;

import com.posapi.application.service.bootstrap.BootstrapService;
import com.posapi.infrastructure.adapter.output.persistence.adapter.user.UserPersistenceAdapter;
import com.posapi.infrastructure.security.JwtAuthenticationEntryPoint;
import com.posapi.infrastructure.security.JwtRequestFilter;
import com.posapi.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        UserPersistenceAdapter.class }))
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000",
        "app.bootstrap.admin.email=admin@test.com",
        "app.bootstrap.admin.password=adminpassword"
})
@ActiveProfiles("test")
class PosApiApplicationTests {

    @MockBean
    private BootstrapService bootstrapService; // Desactivamos el BootstrapService

    @MockBean(name = "customJwtRequestFilter")
    private JwtRequestFilter jwtRequestFilter;

    @MockBean(name = "customJwtAuthenticationEntryPoint")
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean(name = "customUserDetailsService")
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        // El test pasará ahora que el contexto carga correctamente
    }
}
