package com.posapi;
import com.posapi.infrastructure.security.JwtAuthenticationEntryPoint;
import com.posapi.infrastructure.security.JwtRequestFilter;
import com.posapi.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000"
})
@ActiveProfiles("test")
class PosApiApplicationTests {

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
    }

}