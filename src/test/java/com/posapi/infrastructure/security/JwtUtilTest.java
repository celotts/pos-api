package com.posapi.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        setField(jwtUtil, "secret", base64Key);
        setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    void generateAndValidateToken_valid() {
        UserDetails user = new User("testuser", "pwd", Collections.emptyList());
        String token = jwtUtil.generateToken(user);
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token, user));
    }

    @Test
    void validateToken_expired() throws Exception {
        setField(jwtUtil, "expiration", -1000L);
        UserDetails user = new User("user", "pwd", Collections.emptyList());
        String token = jwtUtil.generateToken(user);
        assertFalse(jwtUtil.validateToken(token, user));
    }

    @Test
    void validateToken_tampered() {
        UserDetails user = new User("testuser", "pwd", Collections.emptyList());
        String token = jwtUtil.generateToken(user);
        String tampered = token + "a";
        assertFalse(jwtUtil.validateToken(tampered, user));
    }
}
