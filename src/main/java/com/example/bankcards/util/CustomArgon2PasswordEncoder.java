package com.example.bankcards.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomArgon2PasswordEncoder implements PasswordEncoder {
    private Argon2PasswordEncoder passwordEncoder;

    public CustomArgon2PasswordEncoder() {
        passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
    }

    @Override
    public String encode(CharSequence password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(CharSequence password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}