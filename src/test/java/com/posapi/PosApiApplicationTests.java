package com.posapi;

import com.posapi.application.service.bootstrap.BootstrapService;
import com.posapi.domain.port.output.PasswordEncoderPort;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.product.mapper.ProductRestMapper;
import com.posapi.infrastructure.security.JwtAuthenticationEntryPoint;
import com.posapi.infrastructure.security.JwtRequestFilter;
import com.posapi.infrastructure.security.UserDetailsProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "jwt.secret=clavesecretadebackendedeseguridadsuperlargade64bytes12345",
        "jwt.expiration=86400000",
        "app.bootstrap.admin.email=admin@test.com",
        "app.bootstrap.admin.password=adminpassword"
})
@ActiveProfiles("test")
class PosApiApplicationTests {

    @MockitoBean
    private BootstrapService bootstrapService;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private UserDetailsProvider userDetailsService;

    @MockitoBean
    private PasswordEncoderPort passwordEncoderPort;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private ProductRestMapper productRestMapper;

    @Test
    void contextLoads() {
    }
}
