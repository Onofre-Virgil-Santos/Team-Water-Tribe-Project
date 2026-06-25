package com.watertribe.todo.controller;

import com.watertribe.todo.entity.User;
import com.watertribe.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.watertribe.todo.utility.JwtUtility;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtility jwtUtility;

    /**
     * POST /api/users/register
     * Body: { "username": "alice", "email": "alice@example.com", "password": "secret123" }
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email    = body.get("email");
        String password = body.get("password");

        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (password.length() < 8) {
            return ResponseEntity.badRequest().body(null);
        }

        User response = userService.register(username, email, password);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * POST /login
     * Body: { "username": "alice", "password": "secret123" }
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.login(username, password);
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        String token = jwtUtility.generateToken(user);
        return ResponseEntity.ok(token);
    }
}
