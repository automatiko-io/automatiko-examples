package io.automatiko.examples;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.automatiko.examples.MockEventSource.EventData;
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
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword", data.type);

        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.isuserregistered", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.isuserregistered")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.isuserregistered", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.create", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.create")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.create", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.sendsuccessfulnotification", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.sendsuccessfulnotification")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.sendsuccessfulnotification", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.end", data.type);     
    }
    
    @Test
    public void testAlreadyRegisteredPath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"john.doe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"doe\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        List<EventData> events = eventSource.events();
        assertEquals(1, events.size());
        
        EventData data = events.get(0);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword", data.type);

        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.isuserregistered", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.isuserregistered")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.isuserregistered", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.end", data.type);   
          
    }
    
    @Test
    public void testInvalidDataPath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"john.doe@email.com\",  \"firstName\" : \"john\",  \"lastName\" : \"\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        List<EventData> events = eventSource.events();
        assertEquals(1, events.size());
        
        EventData data = events.get(0);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.notifyinvaliddata", data.type);        
          
    }
    
    @Test
    public void testRegistrationFailurePath() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0")
            .header("ce-source", "test")
            .header("ce-specversion", "1.0")
            .body("{\"user\" : {\"email\" : \"mary.strong@email.com\",  \"firstName\" : \"mary\",  \"lastName\" : \"strong\"}}")
        .when()
            .post("/")
        .then()
            .statusCode(204);
        
        List<EventData> events = eventSource.events();
        assertEquals(1, events.size());
        
        EventData data = events.get(0);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword", data.type);

        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.generateusernameandpassword", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.isuserregistered", data.type);  
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.isuserregistered")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.isuserregistered", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.create", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.create")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.create", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.sendservererrornotification", data.type);   
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .header("ce-id", UUID.randomUUID().toString())
            .header("ce-type", "io.automatiko.serverless.userRegistration.v1_0.sendservererrornotification")
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
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.sendservererrornotification", data.source.split("/")[0]);
        assertEquals("io.automatiko.serverless.userRegistration.v1_0.end", data.type);     
    }
    
    // @formatter:on
}