package com.posapi.domain.port.output;

public interface PasswordEncoderPort {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
