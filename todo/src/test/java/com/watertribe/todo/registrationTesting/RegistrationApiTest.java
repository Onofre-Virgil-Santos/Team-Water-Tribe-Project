package com.watertribe.todo.registrationTesting;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST Assured integration tests for POST /register.
 *
 * These tests run against the live application (http://localhost:8080).
 * Start the Spring Boot server before executing this suite.
 *
 * Scenarios covered:
 *   - Successful registration          → 201 + "Registration successful!"
 *   - Duplicate username               → 409 + "Username already exists"
 *   - Duplicate email                  → 409 + "Email already exists"
 *   - Missing username                 → 400 + required-fields message
 *   - Missing email                    → 400 + required-fields message
 *   - Missing password                 → 400 + required-fields message
 *   - Blank fields                     → 400 + required-fields message
 *   - Empty JSON body                  → 400 + required-fields message
 *   - Password shorter than 8 chars    → 400 + min-length message
 *   - Password exactly 8 chars         → 201 (boundary — accepted)
 *   - Password exactly 7 chars         → 400 (boundary — rejected)
 */
class RegistrationApiTest {

    private static final String BASE_URL       = "http://localhost:8080";
    private static final String REGISTER_PATH  = "/register";

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Generates a unique username so parallel or repeated runs never collide.
     * e.g. "user_3f2a1b"
     */
    private String uniqueUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 6);
    }

    /** Same idea for email. */
    private String uniqueEmail(String username) {
        return username + "@example.com";
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Happy Path
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-R-01: Valid new user returns 201 and success message")
    void registerWithValidData_returns201() {
        String username = uniqueUsername();

        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", username,
                "email",    uniqueEmail(username),
                "password", "password123"
            ))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(201)
            .body(equalTo("Registration successful!"));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Conflict (Duplicate) Failures
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-R-02: Duplicate username returns 409")
    void registerWithDuplicateUsername_returns409() {
        String username = uniqueUsername();
        String email    = uniqueEmail(username);

        // First registration — must succeed
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", email, "password", "password123"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(201);

        // Second registration — same username, different email
        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", username,
                "email",    "other_" + uniqueEmail(username),
                "password", "password123"
            ))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(409)
            .body(equalTo("Username already exists"));
    }

    @Test
    @DisplayName("TC-R-03: Duplicate email returns 409")
    void registerWithDuplicateEmail_returns409() {
        String username = uniqueUsername();
        String email    = uniqueEmail(username);

        // First registration — must succeed
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", email, "password", "password123"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(201);

        // Second registration — different username, same email
        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", "other_" + username,
                "email",    email,
                "password", "password123"
            ))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(409)
            .body(equalTo("Email already exists"));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Input Validation — Missing Fields
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-R-04: Missing username returns 400")
    void registerWithMissingUsername_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("email", "test@example.com", "password", "password123"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username, email and password are required."));
    }

    @Test
    @DisplayName("TC-R-05: Missing email returns 400")
    void registerWithMissingEmail_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", uniqueUsername(), "password", "password123"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username, email and password are required."));
    }

    @Test
    @DisplayName("TC-R-06: Missing password returns 400")
    void registerWithMissingPassword_returns400() {
        String username = uniqueUsername();
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", uniqueEmail(username)))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username, email and password are required."));
    }

    @Test
    @DisplayName("TC-R-07: All fields blank returns 400")
    void registerWithAllBlankFields_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", " ", "email", " ", "password", " "))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username, email and password are required."));
    }

    @Test
    @DisplayName("TC-R-08: Empty JSON body returns 400")
    void registerWithEmptyBody_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username, email and password are required."));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Input Validation — Password Length
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-R-09: Password shorter than 8 characters returns 400")
    void registerWithShortPassword_returns400() {
        String username = uniqueUsername();
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", uniqueEmail(username), "password", "short"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Password must be at least 8 characters."));
    }

    @Test
    @DisplayName("TC-R-10: Password of exactly 7 characters returns 400 (boundary)")
    void registerWithSevenCharPassword_returns400() {
        String username = uniqueUsername();
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", uniqueEmail(username), "password", "1234567"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Password must be at least 8 characters."));
    }

    @Test
    @DisplayName("TC-R-11: Password of exactly 8 characters returns 201 (boundary)")
    void registerWithEightCharPassword_returns201() {
        String username = uniqueUsername();
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", username, "email", uniqueEmail(username), "password", "12345678"))
        .when()
            .post(REGISTER_PATH)
        .then()
            .statusCode(201)
            .body(equalTo("Registration successful!"));
    }
}
