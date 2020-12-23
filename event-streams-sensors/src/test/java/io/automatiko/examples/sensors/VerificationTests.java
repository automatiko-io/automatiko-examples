package io.automatiko.examples.sensors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;

@QuarkusTest
public class VerificationTests {
 // @formatter:off
    
    @Inject 
    @Any
    InMemoryConnector connector;
    
    @AfterAll
    public static void preapre() throws IOException {
        
        File processes = new File("target/processes");
        File jobs = new File("target/jobs");
        
        Files.walk(processes.toPath())
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
        
        Files.walk(jobs.toPath())
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
    }
    
    @Test
    public void testProcessExecution() {
        String humidity = "{\"ts\":1, \"val\" : 45.0}";
        String water = "{\"ts\":1, \"val\" : 29.0}";
        
        InMemorySource<MqttMessage<byte[]>> channelW = connector.source("water");
        
        InMemorySource<MqttMessage<byte[]>> channelH = connector.source("humidity");
        
        channelW.send(MqttMessage.of("building/B123/kitchen/water", water.getBytes()));
        channelH.send(MqttMessage.of("building/B123/kitchen/humidity", humidity.getBytes()));
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks/B123-kitchen")
        .then()
            .statusCode(200).body("id", is("B123-kitchen")).extract().as(Map.class);
        
        List<?> waterBucket = (List<?>) data.get("measurements");
        Object humidityValue = data.get("humidity");
        
        assertEquals(1, waterBucket.size());
        assertNotNull(humidityValue);
        
        // let's push data for living room
        humidity = "{\"ts\":1, \"val\" : 45.0}";
        water = "{\"ts\":1, \"val\" : 29.0}";
        
        channelW.send(MqttMessage.of("building/B123/livingroom/temperature", water.getBytes()));
        channelH.send(MqttMessage.of("building/B123/livingroom/humidity", humidity.getBytes()));
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(2));
        
        data = given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks/B123-livingroom")
        .then()
            .statusCode(200).body("id", is("B123-livingroom")).extract().as(Map.class);
        
        waterBucket = (List<?>) data.get("measurements");
        humidityValue = data.get("humidity");
        
        assertEquals(1, waterBucket.size());
        assertNotNull(humidityValue);
        
        // abort instance for kitchen
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/waterleaks/B123-kitchen")
        .then()
            .statusCode(200);
        
        // abort instance for livingroom
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/waterleaks/B123-livingroom")
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @Test
    public void testProcessExecutionHumidityHigh() {

        String humidity = "{\"ts\":1, \"val\" : 95.0}";
        String water = "{\"ts\":1, \"val\" : 29.0}";
        
        InMemorySource<MqttMessage<byte[]>> channelW = connector.source("water");
        
        InMemorySource<MqttMessage<byte[]>> channelH = connector.source("humidity");
        
        channelW.send(MqttMessage.of("building/B123/kitchen/water", water.getBytes()));
        channelH.send(MqttMessage.of("building/B123/kitchen/humidity", humidity.getBytes()));
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks/B123-kitchen")
        .then()
            .statusCode(200).body("id", is("B123-kitchen")).extract().as(Map.class);
        
        List<?> waterBucket = (List<?>) data.get("measurements");
        Object humidityValue = data.get("humidity");
        
        assertEquals(1, waterBucket.size());
        assertNotNull(humidityValue);

        // since humidity value was high there should be task assigned to technicians
        
        @SuppressWarnings("unchecked")
        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/waterleaks/B123-kitchen/tasks?group=technicians")
            .then()
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskId = taskInfo.get(0).get("id");
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("intervention", taskName);
        
        given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{}")
        .when()
            .post("/waterleaks/B123-kitchen/" + taskName + "/" + taskId + "?group=technicians")
        .then()
            .statusCode(200).body("id", is("B123-kitchen"));
        
        // abort instance for kitchen
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/waterleaks/B123-kitchen")
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @Test
    public void testProcessExecutionReportSent() throws InterruptedException {
        System.setProperty("water_leaks_timer", "PT2S");
        String humidity = "{\"ts\":1, \"val\" : 45.0}";
        String water = "{\"ts\":1, \"val\" : 29.0}";
        
        InMemorySource<MqttMessage<byte[]>> channelW = connector.source("water");
        
        InMemorySource<MqttMessage<byte[]>> channelH = connector.source("humidity");
        
        channelW.send(MqttMessage.of("building/B123/kitchen/water", water.getBytes()));
        channelH.send(MqttMessage.of("building/B123/kitchen/humidity", humidity.getBytes()));
        
        InMemorySink<MqttMessage<byte[]>> channelReports = connector.sink("reports");
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks/B123-kitchen")
        .then()
            .statusCode(200).body("id", is("B123-kitchen")).extract().as(Map.class);
        
        List<?> waterBucket = (List<?>) data.get("measurements");
        Object humidityValue = data.get("humidity");
        
        assertEquals(1, waterBucket.size());
        assertNotNull(humidityValue);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/waterleaks/B123-kitchen/tasks?group=technicians")
            .then()
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(0, taskInfo.size());
        
        long elapsed = 3000;
        
        while(elapsed >= 0) {
            elapsed -= 1000;
            Thread.sleep(1000);
            int size = given()
                .accept(ContentType.JSON)
            .when()
                .get("/waterleaks")
            .then().statusCode(200)
                .extract().path("$.size()");
            
            if (size == 0) {
                break;
            }
        }
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/waterleaks")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
        List<? extends Message<MqttMessage<byte[]>>> received = channelReports.received();
        assertEquals(1, received.size());
        channelReports.clear();
    }
    
    @Test
    public void testBuildingReportsProcessExecution() {
        String report = "{\"average\":25.0,\"min\":25.0,\"max\":25.0,\"leakDetected\":false}";
        
        InMemorySource<MqttMessage<byte[]>> channelR = connector.source("buildingreports");
                
        channelR.send(MqttMessage.of("reports/B123/kitchen/hourly", report.getBytes()));
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/reports")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = given()
            .accept(ContentType.JSON)
        .when()
            .get("/reports/B123")
        .then()            
            .statusCode(200).body("id", is("B123")).extract().as(Map.class);
        
        List<?> waterBucket = (List<?>) data.get("waterReports");       
        
        assertEquals(1, waterBucket.size());        
        // let's push data for living room
        report = "{\"average\":45.0,\"min\":25.0,\"max\":65.0,\"leakDetected\":false}";
        
        channelR.send(MqttMessage.of("reports/B123/livingroom/hourly", report.getBytes()));
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/reports")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        data = given()
            .accept(ContentType.JSON)
        .when()
            .get("/reports/B123")
        .then()
            .statusCode(200).body("id", is("B123")).extract().as(Map.class);
        
        waterBucket = (List<?>) data.get("waterReports");
        
        assertEquals(2, waterBucket.size());
        
        // abort instance for kitchen
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/reports/B123")
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/reports")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
 // @formatter:on
}
