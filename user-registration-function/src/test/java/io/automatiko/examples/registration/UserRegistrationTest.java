package io.automatiko.examples.registration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
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
            .post("/userregistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(100), "user.username", equalTo("mstrong"), "user.password", equalTo("S3cr3T"));
    }
    
    @Test
    public void testAlreadyRegisteredPath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"john.doe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"doe\"}}")
        .when()
            .post("/userregistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(300), "user.username", equalTo("jdoe"), "user.password", equalTo("S3cr3T"));
       
          
    }
    
    @Test
    public void testInvalidDataPath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"john.doe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"\"}}")
        .when()
            .post("/userregistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(400), "user.username", nullValue(), "user.password", nullValue());
           
          
    }
    
    @Test
    public void testRegistrationFailurePath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"mary.strong@email.com\",  \"firstName\" : \"mary\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/userregistration")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(500), "user.username", equalTo("mstrong"), "user.password", equalTo("S3cr3T"));
           
    }
    
    @Test
    public void testSuccessPathGet() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)            
        .when()
            .get("/userregistration?user.email=mike.strong@email.com&user.firstName=mike&user.lastName=strong")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(100), "user.username", equalTo("mstrong"), "user.password", equalTo("S3cr3T"));
    }
    
    @Test
    public void testAlreadyRegisteredPathGet() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/userregistration?user.email=john.doe@email.com&user.firstName=john&user.lastName=doe")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(300), "user.username", equalTo("jdoe"), "user.password", equalTo("S3cr3T"));
       
          
    }
    
    @Test
    public void testInvalidDataPathGet() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/userregistration?user.email=john.doe@email.com&user.firstName=john&user.lastName=")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(400), "user.username", nullValue(), "user.password", nullValue());
           
          
    }
    
    @Test
    public void testRegistrationFailurePathGet() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"user\" : {\"email\" : \"mary.strong@email.com\",  \"firstName\" : \"mary\",  \"lastName\" : \"strong\"}}")
        .when()
            .get("/userregistration?user.email=mary.strong@email.com&user.firstName=mary&user.lastName=strong")
        .then()
            .statusCode(200)
            .body("id", notNullValue(), "user.userStatus", equalTo(500), "user.username", equalTo("mstrong"), "user.password", equalTo("S3cr3T"));
           
    }
    
    // @formatter:on
}