package io.automatiko.examples.registration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(WiremockPetstore.class)
public class UserRegistrationTest {

    @Inject
    TestResults results;

    @BeforeEach
    public void prepare() {
        results.clear();
    }

    // @formatter:off
    @Test
    public void testSuccessPath() throws InterruptedException {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "google.cloud.pubsub.topic.v1.messagePublished")
            .header("ce-source", "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"mike.strong@email.com\",  \"firstName\" : \"mike\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        results.waitForEvent(1, 5);
        
        assertEquals("userregistered", results.getEventType());
        assertEquals("mstrong", results.getData().get("user").get("username").asText());
        assertEquals("S3cr3T", results.getData().get("user").get("password").asText());
    }
    
    @Test
    public void testAlreadyRegisteredPath() throws InterruptedException {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "google.cloud.pubsub.topic.v1.messagePublished")
            .header("ce-source", "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"john.doe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"doe\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        results.waitForEvent(1, 5);
        
        assertEquals("already-registered", results.getEventType());
        assertEquals("jdoe", results.getData().get("user").get("username").asText());
        assertEquals("S3cr3T", results.getData().get("user").get("password").asText());
    }
    
    @Test
    public void testInvalidDataPath() throws InterruptedException {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "google.cloud.pubsub.topic.v1.messagePublished")
            .header("ce-source", "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"john.doe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);

        results.waitForEvent(1, 5);
        
        assertEquals("invalid", results.getEventType()); 
        assertFalse(results.getData().get("user").hasNonNull("username"));
        assertFalse(results.getData().get("user").hasNonNull("password"));
    }
    
    @Test
    public void testRegistrationFailurePath() throws InterruptedException {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "google.cloud.pubsub.topic.v1.messagePublished")
            .header("ce-source", "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"mary.strong@email.com\",  \"firstName\" : \"mary\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        results.waitForEvent(1, 5);
        
        assertEquals("failed", results.getEventType());
        assertEquals("mstrong", results.getData().get("user").get("username").asText());
        assertEquals("S3cr3T", results.getData().get("user").get("password").asText());
    }
    
    // @formatter:on
}