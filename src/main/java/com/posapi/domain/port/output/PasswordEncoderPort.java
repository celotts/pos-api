package com.posapi.domain.port.output;

public interface PasswordEncoderPort {
    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);

    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
