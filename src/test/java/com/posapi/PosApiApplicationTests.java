package com.posapi;

import com.posapi.infrastructure.security.JwtRequestFilter;
import com.posapi.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000"
})
@ActiveProfiles("test")
class PosApiApplicationTests {

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void contextLoads() {
    }

}