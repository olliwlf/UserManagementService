package com.task.ums;

import com.task.ums.models.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the {@link UserResource} class.
 *
 * @see UserResource
 * @see User
 **/
public class UserResourceTest {
    /** Specify the uri for rest assured. */
    @BeforeEach
    public void setUp() {
        // Set the base URL for REST Assured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080; // port of the Wildfly server
        RestAssured.basePath = "/UMS-1.0/api";
    }

    /**
     * Tested Method: GetAllUsers
     * Result: StatusCode 200
     * */
    @Test
    public void whenGetAllUsers_then200IsReceived() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200);
    }

    /**
     * Tested Method: GetUserById
     * Given: User with Id 999 does not exist.
     * Result: StatusCode 404 + Error Message
     * */
    @Test
    public void givenUserNotExists_whenGetUser_then404IsReceived() {
        given()
                .when()
                .get("/users/999")
                .then()
                .statusCode(404)
                .body(equalTo("There is no user with the ID 999."));
    }

    /**
     * Tested Method: AddUser
     * Result: StatusCode 201 + created User
     * */
    @Test
    public void whenAddUser_then201IsReceived() {
        String newUser = "{ \"firstname\": \"Max\", \"lastname\": \"Mustermann\", \"email\": \"max.mustermann@example.com\", \"birthday\": \"2000-01-01\", \"password\": \"password123\" }";
        given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("firstname", equalTo("Max"))
                .body("lastname", equalTo("Mustermann"))
                .body("email", equalTo("max.mustermann@example.com"));
    }

    /**
     * Tested Method: AddUser
     * Given: Invalid User with no lastname (lastname is mandatory).
     * Result: StatusCode 400
     * */
    @Test
    public void givenInvalidUserWithNoLastname_whenAddUser_then400IsReceived() {
        String newUser = "{ \"firstname\": \"Max\", \"email\": \"max.mustermann@example.com\", \"birthday\": \"2000-01-01\", \"password\": \"password123\" }";
        given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(400);
    }

    /**
     * Tested Method: UpdateUser
     * Given: User with the ID exists.
     * Result: StatusCode 200 + updated User
     * */
    @Test
    public void givenUserExists_whenUpdateUser_then200IsReceived() {
        // First add a user to update
        String newUser = "{ \"firstname\": \"Max\", \"lastname\": \"Mustermann\", \"email\": \"max.mustermann@example.com\", \"birthday\": \"2000-01-01\", \"password\": \"password123\" }";
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        Long userId = response.jsonPath().getLong("id");

        // Update the user
        String updatedUser = "{ \"firstname\": \"Maria\", \"lastname\": \"Musterfrau\", \"email\": \"maria.musterfrau@example.com\", \"birthday\": \"2002-01-01\", \"password\": \"newpassword123\" }";
        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Maria"))
                .body("lastname", equalTo("Musterfrau"))
                .body("email", equalTo("maria.musterfrau@example.com"));
    }

    /**
     * Tested Method: UpdateUser
     * Given: New user data is invalid (email format is false).
     * Result: StatusCode 400
     * */
    @Test
    public void givenUserExists_whenUpdateUserWithInvalidData_then200IsReceived() {
        // First add a user to update
        String newUser = "{ \"firstname\": \"Max\", \"lastname\": \"Mustermann\", \"email\": \"max.mustermann@example.com\", \"birthday\": \"2000-01-01\", \"password\": \"password123\" }";
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        Long userId = response.jsonPath().getLong("id");

        // Update the user (invalid data: wrong email-format)
        String updatedUser = "{ \"firstname\": \"Maria\", \"lastname\": \"Musterfrau\", \"email\": \"maria.musterfrau\", \"birthday\": \"2002-01-01\", \"password\": \"newpassword123\" }";
        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(400);
    }

    /**
     * Tested Method: DeleteUser
     * Given: User exists.
     * Result: StatusCode 201
     * */
    @Test
    public void givenUserExists_whenDeleteUser_then201IsReceived() {
        // First add a user to delete
        String newUser = "{ \"firstname\": \"Max\", \"lastname\": \"Mustermann\", \"email\": \"max.mustermann@example.com\", \"birthday\": \"2000-01-01\", \"password\": \"password123\" }";
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();

        Long userId = response.jsonPath().getLong("id");

        // Delete the user
        given()
                .when()
                .delete("/users/" + userId)
                .then()
                .statusCode(204);
    }

    /**
     * Tested Method: UpdateUser
     * Given: User doesn't exist.
     * Result: StatusCode 404 + Error Message
     * */
    @Test
    public void givenUserNotExists_whenUpdateUser_then404IsReceived() {
        String updatedUserJson = "{ \"firstname\": \"NonExistent\", \"lastname\": \"User\", \"email\": \"nonexistent.user@example.com\", \"birthday\": \"1990-01-01\", \"password\": \"password123\" }";
        given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .when()
                .put("/users/999")
                .then()
                .statusCode(404)
                .body(equalTo("There is no user with the ID 999."));
    }

    /**
     * Tested Method: DeleteUser
     * Given: User doesn't exist.
     * Result: StatusCode 404 + Error Message
     * */
    @Test
    public void givenUserNotExists_whenDeleteUser_then404IsReceived() {
        given()
                .when()
                .delete("/users/999")
                .then()
                .statusCode(404)
                .body(equalTo("There is no user with the ID 999."));
    }
}
