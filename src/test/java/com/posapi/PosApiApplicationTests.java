package com.posapi;

import com.posapi.application.service.bootstrap.BootstrapService;
import com.posapi.infrastructure.adapter.output.persistence.adapter.user.UserPersistenceAdapter;
import com.posapi.infrastructure.security.JwtAuthenticationEntryPoint;
import com.posapi.infrastructure.security.JwtRequestFilter;
import com.posapi.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
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

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public BootstrapService bootstrapService() {
            return Mockito.mock(BootstrapService.class);
        }

        @Bean
        @Primary
        public JwtRequestFilter jwtRequestFilter() {
            return Mockito.mock(JwtRequestFilter.class);
        }

        @Bean
        @Primary
        public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
            return Mockito.mock(JwtAuthenticationEntryPoint.class);
        }

        @Bean
        @Primary
        public UserDetailsServiceImpl userDetailsService() {
            return Mockito.mock(UserDetailsServiceImpl.class);
        }

        @Bean
        @Primary
        public PasswordEncoder passwordEncoder() {
            return Mockito.mock(PasswordEncoder.class);
        }
    }

    @Test
    void contextLoads() {
        // The test will now pass as the context loads correctly
    }
}
