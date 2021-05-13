package io.spring.infrastructure.service;

import io.spring.core.user.EncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class NaiveEncryptService implements EncryptService {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public NaiveEncryptService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encrypt(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean check(String checkPassword, String realPassword) {
        return passwordEncoder.matches(checkPassword, realPassword);
    }
}
