package io.automatiko.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(WiremockPetstore.class)
public class UserRegistrationTest {

    // @formatter:off
    @Test
    public void testSuccessPath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"mike.strong@email.com\",  \"firstName\" : \"mike\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/userRegistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.username", equalTo("mstrong"), "user.userStatus", equalTo(100));
        
        given()
        .accept(ContentType.JSON)
        .when()
        .get("/userRegistration")
        .then().statusCode(200)
        .body("$.size()", is(0));
    }
    
    @Test
    public void testInvalidData() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"mike.strong@email.com\",  \"firstName\" : \"mike\"}}")
        .when()
            .post("/userRegistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.username", nullValue(), "user.userStatus", equalTo(400));
        
        given()
        .accept(ContentType.JSON)
        .when()
        .get("/userRegistration")
        .then().statusCode(200)
        .body("$.size()", is(0));
    }
    
    @Test
    public void testAlreadyRegistered() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"jdoe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"doe\"}}")
        .when()
            .post("/userRegistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.username", equalTo("jdoe"), "user.userStatus", equalTo(300));
        
        given()
        .accept(ContentType.JSON)
        .when()
        .get("/userRegistration")
        .then().statusCode(200)
        .body("$.size()", is(0));
    }
    // @formatter:on
}