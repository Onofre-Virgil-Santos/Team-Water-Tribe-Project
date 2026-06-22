package com.watertribe.todo.service;

import com.watertribe.todo.entity.User;
import com.watertribe.todo.exception.RegistrationFailure;
import com.watertribe.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new user and returns the saved entity.
     */
    public User register(String username, String email, String password) {
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
                .build();

        return userRepository.save(user);
    }

    /**
     * Validates credentials. Returns the User on success, null if username
     * not found or password doesn't match.
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }
}
