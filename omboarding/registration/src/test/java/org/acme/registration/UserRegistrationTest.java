package org.acme.registration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import jakarta.inject.Inject;

import org.acme.registration.MockEventSource.EventData;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(WiremockPetstore.class)
public class UserRegistrationTest {

    @Inject
    MockEventSource eventSource;

    // @formatter:off
    @Test
    public void testSuccessPath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"mike.strong@email.com\",  \"firstName\" : \"mike\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        List<EventData> events = eventSource.events();
        assertEquals(1, events.size());
        
        EventData data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.generateusernameandpassword", data.type);

        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.generateusernameandpassword")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.generateusernameandpassword", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.getuser", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.getuser")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.getuser", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.registeruser", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.registeruser")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.registeruser", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.notifyregistered", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.notifyregistered")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.BackgroundCheck", data.source.split("/")[0]);
        assertEquals("org.acme.background.checks", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "org.acme.background.checks.ok")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body("{\"status\" : \"OK\",\"username\" : \"mstrong\",\"comments\" : []}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.backgroundcheckok_0", data.source.split("/")[0]);
        assertEquals("com.acme.notifications.email", data.type);  
    }
    
    @Test
    public void testFailurePath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"mike.strong@email.com\",  \"firstName\" : \"mike\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        List<EventData> events = eventSource.events();
        assertEquals(1, events.size());
        
        EventData data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.generateusernameandpassword", data.type);

        String workflowId = data.source.split("/")[1];
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.generateusernameandpassword")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.generateusernameandpassword", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.getuser", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.getuser")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.getuser", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.registeruser", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.registeruser")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.registeruser", data.source.split("/")[0]);
        assertEquals("io.automatiko.examples.userRegistration.notifyregistered", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.examples.userRegistration.notifyregistered")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body(data.getData())
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.BackgroundCheck", data.source.split("/")[0]);
        assertEquals("org.acme.background.checks", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "org.acme.background.checks.failed")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .header("ce-subject", workflowId)
            .body("{\"status\" : \"Failed\",\"username\" : \"mstrong\",\"comments\" : [\"incorrect username\"]}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        events = eventSource.events();
        assertEquals(1, events.size());
        
        data = events.get(0);
        assertEquals("io.automatiko.examples.userRegistration.backgroundcheckfailed_1", data.source.split("/")[0]);
        assertEquals("com.acme.notifications.email", data.type);  
    }
    
    // @formatter:on
}