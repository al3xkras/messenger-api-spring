package com.al3xkras.messenger.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MessengerPasswordEncoder implements PasswordEncoder {
    private final List<String> activeProfiles;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public MessengerPasswordEncoder(Environment environment) {
        activeProfiles = Arrays.asList(environment.getActiveProfiles());
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (activeProfiles.stream().anyMatch(x->x.contains("test")))
            return rawPassword.toString();
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (activeProfiles.stream().anyMatch(x->x.contains("test")))
            return rawPassword.toString().equals(encodedPassword);
        return bCryptPasswordEncoder.matches(rawPassword,encodedPassword);
    }
}
