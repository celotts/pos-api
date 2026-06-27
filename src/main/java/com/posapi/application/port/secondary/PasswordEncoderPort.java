package com.posapi.application.port.secondary;

public interface PasswordEncoderPort {
    String encode(CharSequence rawPassword);
    boolean maches(CharSequence rawPassword);
}
