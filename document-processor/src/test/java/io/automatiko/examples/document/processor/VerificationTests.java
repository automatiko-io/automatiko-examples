package io.automatiko.examples.document.processor;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.automatiko.engine.api.event.EventPublisher;
import io.automatiko.engine.services.event.impl.CountDownProcessInstanceEventPublisher;
import io.automatiko.engine.services.utils.IoUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@SuppressWarnings({ "unchecked", "rawtypes" })
@QuarkusTest
public class VerificationTests {
    // @formatter:off
    private CountDownProcessInstanceEventPublisher execCounter = new CountDownProcessInstanceEventPublisher();

    @Produces
    @Singleton
    public EventPublisher publisher() {
        return execCounter;
    }

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
    public void testSingleGreetingFileProcessing() throws Exception {
        execCounter.reset(1);

        Files.write(new File("target" + File.separator + "documents" + File.separator + "hello.txt").toPath(),
                "hello every body".getBytes());

        execCounter.waitTillCompletion(3);
    }

    @Test
    public void testReportFileProcessingAbort() throws Exception {
        execCounter.reset(1);

        StringBuilder data = new StringBuilder("Report");
        for (int i = 0; i < 50; i++) {
            data.append("Content of the report by line \n");
        }

        Files.write(new File("target" + File.separator + "documents" + File.separator + "report.txt").toPath(),
                data.toString().getBytes());

        execCounter.waitTillCompletion(3);
        
        List instances = given()
            .accept(ContentType.JSON)
        .when()
            .get("/text")
        .then().statusCode(200)
            .body("$.size()", is(1)).extract().as(List.class);
            
        
        Map<String, Object> instanceData = (Map<String, Object>) instances.get(0);
        
        String id  = (String) instanceData.get("id");
        
        // abort instance
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/text/" + id)
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/text")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
        
    @Disabled
    @Test
    public void testReportFileProcessingApprove() throws Exception {
        execCounter.reset(1);

        StringBuilder data = new StringBuilder("Report");
        for (int i = 0; i < 10; i++) {
            data.append("Content of the report by line \n");
        }
        File file = new File("target" + File.separator + "documents" + File.separator + "report.tmp");
        Files.write(file.toPath(),
                data.toString().getBytes());
        file.renameTo(new File("target" + File.separator + "documents" + File.separator + "report.txt"));

        execCounter.waitTillCompletion(3);
        
        List instances = given()
            .accept(ContentType.JSON)
        .when()
            .get("/text")
        .then().statusCode(200)
            .body("$.size()", is(1)).extract().as(List.class);
            
        
        Map<String, Object> instanceData = (Map<String, Object>) instances.get(0);
        
        String id  = (String) instanceData.get("id");
        
        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/text/" + id + "/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskId = taskInfo.get(0).get("id");
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("approveReport", taskName);
        
        String payload = "{}";
        given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
        .when()
            .post("/text/" + id + "/" + taskName + "/" + taskId)
        .then()
            .statusCode(200).body("id", is(id));  
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/text")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @Test
    public void testReportsFileProcessingApprove() throws Exception {
        execCounter.reset(1);

        StringBuilder data = new StringBuilder("Report");
        for (int i = 0; i < 50; i++) {
            data.append("Content of the report by line \n");
        }

        File file = new File("target" + File.separator + "documents" + File.separator + "reports2.tmp");
        Files.write(file.toPath(),
                IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/a.zip")));
        file.renameTo(new File("target" + File.separator + "documents" + File.separator + "reports2.zip"));

        execCounter.waitTillCompletion(3);
        
        List instances = given()
            .accept(ContentType.JSON)
        .when()
            .get("/zip")
        .then().statusCode(200)
            .body("$.size()", is(1)).extract().as(List.class);
            
        
        Map<String, Object> instanceData = (Map<String, Object>) instances.get(0);
        
        String id  = (String) instanceData.get("id");
        
        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/zip/" + id + "/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskName = taskInfo.get(0).get("name");
        String reference = taskInfo.get(0).get("reference");
        
        assertEquals("approveReport", taskName);
        
        String payload = "{}";
        given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
        .when()
            .post("/zip/" + id + "/" + reference)
        .then()
            .statusCode(200);  
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/zip")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @Test
    public void testReportsFileProcessingAbort() throws Exception {
        execCounter.reset(1);

        StringBuilder data = new StringBuilder("Report");
        for (int i = 0; i < 50; i++) {
            data.append("Content of the report by line \n");
        }
        File file = new File("target" + File.separator + "documents" + File.separator + "reports.tmp");
        Files.write(file.toPath(),
                IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/a.zip")));
        file.renameTo(new File("target" + File.separator + "documents" + File.separator + "reports.zip"));

        execCounter.waitTillCompletion(3);
        
        List instances = given()
            .accept(ContentType.JSON)
        .when()
            .get("/zip")
        .then().statusCode(200)
            .body("$.size()", is(1)).extract().as(List.class);
            
        
        Map<String, Object> instanceData = (Map<String, Object>) instances.get(0);
        
        String id  = (String) instanceData.get("id");
        
        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/zip/" + id + "/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("approveReport", taskName);
        
        // abort instance
        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/zip/" + id)
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/zip")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    // @formatter:on
}
