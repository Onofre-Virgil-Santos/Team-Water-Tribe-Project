package com.watertribe.todo.loginTesting;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * REST Assured integration tests for POST /login.
 *
 * These tests run against the live application (http://localhost:8080).
 * Start the Spring Boot server before executing this suite.
 *
 * Scenarios covered mirror Login.feature:
 *   - Successful login → 200 + JWT string
 *   - Wrong password   → 401
 *   - Unknown username → 401
 *   - Missing fields   → 400
 *   - Blank fields     → 400
 *   - Empty body       → 400
 */
class LoginApiTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    // Credentials for the seeded test user
    private static final String VALID_USERNAME = "alice";
    private static final String VALID_EMAIL    = "alice@example.com";
    private static final String VALID_PASSWORD = "password123";

    // ──────────────────────────────────────────────────────────────────────────
    // Setup
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Register the test user once for the entire suite.
     * If the username already exists the server returns 409 — we ignore that
     * so re-runs don't break the suite.
     */
    @BeforeAll
    static void registerTestUser() {
        RestAssured.baseURI = BASE_URL;

        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "username", VALID_USERNAME,
                "email",    VALID_EMAIL,
                "password", VALID_PASSWORD
            ))
        .when()
            .post(REGISTER_PATH)
        .then()
            // 201 = newly created, 409 = already exists — both are fine here
            .statusCode(anyOf(is(201), is(409)));
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Happy Path
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-L-01: Valid credentials return 200 and a JWT token")
    void loginWithValidCredentials_returns200AndJwt() {
        String token =
            given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", VALID_USERNAME, "password", VALID_PASSWORD))
            .when()
                .post(LOGIN_PATH)
            .then()
                .statusCode(200)
                .extract().asString();

        // A JWT has exactly three Base64url segments separated by dots
        assertNotNull(token, "Token must not be null");
        assertFalse(token.isBlank(), "Token must not be blank");
        assertEquals(3, token.split("\\.").length,
            "Token should have three dot-separated segments (header.payload.signature)");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Authentication Failures
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-L-02: Wrong password returns 401 with error message")
    void loginWithWrongPassword_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", VALID_USERNAME, "password", "wrongpasswd"))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(401)
            .body(equalTo("Invalid username or password"));
    }

    @Test
    @DisplayName("TC-L-03: Non-existent username returns 401 with error message")
    void loginWithUnknownUsername_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", "ghost_user", "password", VALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(401)
            .body(equalTo("Invalid username or password"));
    }

    @Test
    @DisplayName("TC-L-04: Both username and password wrong returns 401")
    void loginWithBothFieldsWrong_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", "nobody", "password", "badpassword"))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(401)
            .body(equalTo("Invalid username or password"));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Input Validation
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-L-05: Missing username returns 400")
    void loginWithMissingUsername_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("password", VALID_PASSWORD))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username and password are required."));
    }

    @Test
    @DisplayName("TC-L-06: Missing password returns 400")
    void loginWithMissingPassword_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", VALID_USERNAME))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username and password are required."));
    }

    @Test
    @DisplayName("TC-L-07: Blank username and password returns 400")
    void loginWithBlankFields_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("username", " ", "password", " "))
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username and password are required."));
    }

    @Test
    @DisplayName("TC-L-08: Empty JSON body returns 400")
    void loginWithEmptyBody_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post(LOGIN_PATH)
        .then()
            .statusCode(400)
            .body(equalTo("Username and password are required."));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Token Structure Validation
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-L-09: JWT header is Base64url-encoded JSON with alg and typ")
    void loginToken_hasValidJwtHeader() throws Exception {
        String token =
            given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", VALID_USERNAME, "password", VALID_PASSWORD))
            .when()
                .post(LOGIN_PATH)
            .then()
                .statusCode(200)
                .extract().asString();

        // Decode the header (first segment)
        String headerB64 = token.split("\\.")[0];
        // Pad to a multiple of 4 so Java's decoder is happy
        String padded = headerB64 + "=".repeat((4 - headerB64.length() % 4) % 4);
        String header = new String(java.util.Base64.getUrlDecoder().decode(padded));

        assertTrue(header.contains("\"alg\""), "Header should contain 'alg' claim");
        assertTrue(header.contains("\"typ\""), "Header should contain 'typ' claim");
    }

    @Test
    @DisplayName("TC-L-10: JWT payload contains username claim")
    void loginToken_payloadContainsUsername() throws Exception {
        String token =
            given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", VALID_USERNAME, "password", VALID_PASSWORD))
            .when()
                .post(LOGIN_PATH)
            .then()
                .statusCode(200)
                .extract().asString();

        // Decode the payload (second segment)
        String payloadB64 = token.split("\\.")[1];
        String padded = payloadB64 + "=".repeat((4 - payloadB64.length() % 4) % 4);
        String payload = new String(java.util.Base64.getUrlDecoder().decode(padded));

        assertTrue(payload.contains("\"username\""),
            "Payload should contain custom 'username' claim");
        assertTrue(payload.contains(VALID_USERNAME),
            "Payload should contain the logged-in user's username value");
    }
}
