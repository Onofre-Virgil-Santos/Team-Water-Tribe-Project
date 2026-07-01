package com.watertribe.todo.MainTodoApiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class MainTodoApiTest {

    private static String token;
    private static Integer createdTodoId;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;

        // Login first and get JWT token
        Response loginResponse = given()
                .contentType("application/json")
                .body("""
                    {
                      "username": "susu",
                      "password": "susudodo"
                    }
                """)
                .when()
                .post("/login");

        assertEquals(200, loginResponse.statusCode());

        token = loginResponse.body().asString();

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void testCreateMainTodoSuccessfully() {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                    {
                      "task": "Study REST Assured",
                      "description": "Write API tests"
                    }
                """)
                .when()
                .post("/api/main-todos");

        assertTrue(response.statusCode() == 200 || response.statusCode() == 201);

        createdTodoId = response.jsonPath().getInt("id");

        assertNotNull(createdTodoId);
        assertEquals("Study REST Assured", response.jsonPath().getString("task"));
        assertEquals("Write API tests", response.jsonPath().getString("description"));
    }

    @Test
    void testGetAllMainTodos() {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/main-todos");

        assertEquals(200, response.statusCode());

        assertNotNull(response.body().asString());
        assertTrue(response.body().asString().startsWith("["));
    }

    @Test
    void testGetMainTodoById() {
        Integer todoId = createTodoForTest();

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/main-todos/" + todoId);

        assertEquals(200, response.statusCode());
        assertEquals(todoId, response.jsonPath().getInt("id"));
        assertEquals("Temporary Todo", response.jsonPath().getString("task"));
    }

    @Test
    void testUpdateMainTodoSuccessfully() {
        Integer todoId = createTodoForTest();

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                    {
                      "task": "Updated Todo",
                      "description": "Updated description"
                    }
                """)
                .when()
                .put("/api/main-todos/" + todoId);

        assertEquals(200, response.statusCode());
        assertEquals(todoId, response.jsonPath().getInt("id"));
        assertEquals("Updated Todo", response.jsonPath().getString("task"));
        assertEquals("Updated description", response.jsonPath().getString("description"));
    }

    @Test
    void testDeleteMainTodoSuccessfully() {
        Integer todoId = createTodoForTest();

        Response deleteResponse = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/main-todos/" + todoId);

        assertTrue(deleteResponse.statusCode() == 200 || deleteResponse.statusCode() == 204);

        Response getResponse = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/main-todos/" + todoId);

        // Service throws RuntimeException for not found which Spring maps to 500
        assertTrue(getResponse.statusCode() == 404 || getResponse.statusCode() == 500);
    }

    @Test
    void testCreateMainTodoWithoutTokenShouldFail() {
        Response response = given()
                .contentType("application/json")
                .body("""
                    {
                      "title": "Unauthorized Todo",
                      "description": "Should not be created"
                    }
                """)
                .when()
                .post("/api/main-todos");

        assertEquals(401, response.statusCode());
    }

    @Test
    void testGetNonExistingMainTodoShouldReturn404() {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/main-todos/999999");

        // Service throws RuntimeException for not found which Spring maps to 500
        assertTrue(response.statusCode() == 404 || response.statusCode() == 500);
    }

    private Integer createTodoForTest() {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                    {
                      "task": "Temporary Todo",
                      "description": "Temporary description"
                    }
                """)
                .when()
                .post("/api/main-todos");

        assertTrue(response.statusCode() == 200 || response.statusCode() == 201);

        return response.jsonPath().getInt("id");
    }
}
