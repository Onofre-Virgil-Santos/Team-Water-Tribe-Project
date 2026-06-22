package com.watertribe.todo.service;

import com.watertribe.todo.entity.User;
import com.watertribe.todo.exception.RegistrationFailure;
import com.watertribe.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     *
     * @param username the desired username
     * @param email    the user's email address
     * @param password the plain-text password (will be BCrypt-hashed)
     * @return a map containing the saved user's id, username, and email
     */
    public Map<String, Object> register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RegistrationFailure("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationFailure("Email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .roles(List.of("ROLE_USER"))
                .build();

        User saved = userRepository.save(user);

        return Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "email", saved.getEmail()
        );
    }
}
