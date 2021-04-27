package io.automatiko.examples.vacation.requests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class VerificationTests {
 // @formatter:off
    
    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void init() {
        mailbox.clear();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testProcessExecution() {
        String employee = "{\n"
                + "  \"employee\": {\n"
                + "    \"email\": \"mary@email.com\",\n"
                + "    \"firstName\": \"Mary\",\n"
                + "    \"lastName\": \"Jane\",\n"
                + "    \"startedAt\": \"1999-12-26\",\n"
                + "    \"department\": \"finance\"\n"
                + "  }\n"
                + "}";
        String id = "mary@email.com";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(employee)
        .when()
            .post("/vacations")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());
        
        Map data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()
            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        List<?> vrequests = (List<?>) data.get("vrequests");
        Object vacation = data.get("vacation");
        Object emp = data.get("employee");
        
        assertEquals(0, vrequests.size());
        assertNotNull(vacation);
        assertNotNull(emp);
        
        String manager = (String) ((Map<String, Object>) emp).get("manager");
        assertEquals("john@email.com", manager);
        
        int used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(0, used);
        Number eligible = (Number) ((Map<String, Object>) vacation).get("eligible");
        assertEquals(25.0, eligible);
        
        // since "participants" access policy is used only initiator (mary) or currently assigned tasks owners can see it
        // joe is neither of them so should not see the instance
        given()
            .accept(ContentType.JSON)
            .auth().basic("joe@email.com", "joe")
        .when()
            .get("/vacations")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
        // submit first vacation request
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{\n"
                    + "  \"from\": \"2020-12-26\",\n"
                    + "  \"length\": 5,\n"
                    + "  \"to\": \"2020-12-31\",\n"
                    + "  \"key\": \"xmas\"\n"
                    + "}")
        .when()
            .post("/vacations/" + id + "/submit")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(1, vrequests.size());
        assertNotNull(vacation);
        
        // verify that emails were sent
        List<Mail> sent = mailbox.getMessagesSentTo("mary@email.com");
        assertEquals(1, sent.size());

        sent = mailbox.getMessagesSentTo("john@email.com");
        assertEquals(1, sent.size());

        assertEquals(2, mailbox.getTotalMessagesSent());

        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
                .auth().basic("john@email.com", "john")
            .when()
                .get("/vacations/" + id + "/requests/xmas/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskId = taskInfo.get(0).get("id");
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("approval", taskName);
        
        String referenceId = taskInfo.get(0).get("reference");
        
        Map taskData = given()
                .accept(ContentType.JSON)
                .auth().basic("john@email.com", "john")
            .when()
                .get(referenceId)
            .then()            
                .statusCode(200).extract().as(Map.class);
        assertNotNull(taskData);
        assertNotNull(taskData.get("request"));
        assertNotNull(taskData.get("employee"));
        
        String payload = "{\"approved\" : true}";
        given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("john@email.com", "john")
            .body(payload)
        .when()
            .post("/vacations/" + id + "/requests/xmas/" + taskName + "/" + taskId)
        .then()
            .statusCode(200).body("id", is("xmas"));  
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(1, vrequests.size());
        assertNotNull(vacation);
        
        used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(5, used);
        
        taskInfo = given()
                .accept(ContentType.JSON)
                .auth().basic("mary@email.com", "mary")
            .when()
                .get("/vacations/" + id + "/requests/xmas/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        taskId = taskInfo.get(0).get("id");
        taskName = taskInfo.get(0).get("name");
        
        assertEquals("cancel", taskName);
        
        data = given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{}")
        .when()
            .post("/vacations/" + id + "/requests/xmas/cancel/" + taskId)
        .then()
            .statusCode(200).body("id", is("xmas")).extract().as(Map.class);
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()
            .statusCode(200).body("id", is(id)).extract().as(Map.class);

        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(1, vrequests.size());
        assertNotNull(vacation); 
        
        used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(0, used);
        
        // abort instance
        given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .delete("/vacations/" + id)
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testProcessExecutionMultipleRequestsApproved() {
        String employee = "{\n"
                + "  \"employee\": {\n"
                + "    \"email\": \"mary@email.com\",\n"
                + "    \"firstName\": \"Mary\",\n"
                + "    \"lastName\": \"Jane\",\n"
                + "    \"startedAt\": \"1999-12-26\",\n"
                + "    \"department\": \"finance\"\n"
                + "  }\n"
                + "}";
        String id = "mary@email.com";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(employee)
        .when()
            .post("/vacations")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());
        
        Map data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()
            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        List<?> vrequests = (List<?>) data.get("vrequests");
        Object vacation = data.get("vacation");
        Object emp = data.get("employee");
        
        assertEquals(0, vrequests.size());
        assertNotNull(vacation);
        assertNotNull(emp);
        
        String manager = (String) ((Map<String, Object>) emp).get("manager");
        assertEquals("john@email.com", manager);
        
        int used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(0, used);
        Number eligible = (Number) ((Map<String, Object>) vacation).get("eligible");
        assertEquals(25.0, eligible);       
        
        // submit first vacation request
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{\n"
                    + "  \"from\": \"2020-12-26\",\n"
                    + "  \"length\": 5,\n"
                    + "  \"to\": \"2020-12-31\",\n"
                    + "  \"key\": \"xmas\"\n"
                    + "}")
        .when()
            .post("/vacations/" + id + "/submit")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(1, vrequests.size());
        assertNotNull(vacation);

        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
                .auth().basic("john@email.com", "john")
            .when()
                .get("/vacations/" + id + "/requests/xmas/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskId = taskInfo.get(0).get("id");
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("approval", taskName);
        
        String payload = "{\"approved\" : true}";
        given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("john@email.com", "john")
            .body(payload)
        .when()
            .post("/vacations/" + id + "/requests/xmas/" + taskName + "/" + taskId)
        .then()
            .statusCode(200).body("id", is("xmas"));  
        
        // submit second vacation request
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{\n"
                    + "  \"from\": \"2020-12-26\",\n"
                    + "  \"length\": 5,\n"
                    + "  \"to\": \"2020-12-31\",\n"
                    + "  \"key\": \"travel\"\n"
                    + "}")
        .when()
            .post("/vacations/" + id + "/submit")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());
        
        // submit third vacation request
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{\n"
                    + "  \"from\": \"2020-12-26\",\n"
                    + "  \"length\": 5,\n"
                    + "  \"to\": \"2020-12-31\",\n"
                    + "  \"key\": \"work\"\n"
                    + "}")
        .when()
            .post("/vacations/" + id + "/submit")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());        
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(3, vrequests.size());
        assertNotNull(vacation);  
        
        used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(15, used);
        
        // submit forth vacation request that exceeds available vacation days
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{\n"
                    + "  \"from\": \"2020-12-26\",\n"
                    + "  \"length\": 15,\n"
                    + "  \"to\": \"2020-12-31\",\n"
                    + "  \"key\": \"training\"\n"
                    + "}")
        .when()
            .post("/vacations/" + id + "/submit")
        .then().statusCode(200)        
        .body("id", equalTo(id), "employee", notNullValue());        
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(3, vrequests.size());
        assertNotNull(vacation);  
        
        used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(15, used);
        
        taskInfo = given()
            .accept(ContentType.JSON)
            .auth().basic("john@email.com", "john")
        .when()
            .get("/vacations/" + id + "/requests/work/tasks")
        .then()
            
            .statusCode(200)
            .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        taskId = taskInfo.get(0).get("id");
        taskName = taskInfo.get(0).get("name");
        
        assertEquals("approval", taskName);
        
        payload = "{\"approved\" : false}";
        given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("john@email.com", "john")
            .body(payload)
        .when()
            .post("/vacations/" + id + "/requests/work/" + taskName + "/" + taskId)
        .then()
            .statusCode(200);
        
        data = given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations/" + id)
        .then()            
            .statusCode(200).body("id", is(id)).extract().as(Map.class);
        
        vrequests = (List<?>) data.get("vrequests");
        vacation = data.get("vacation");
        
        assertEquals(3, vrequests.size());
        assertNotNull(vacation);  
        
        used = (int) ((Map<String, Object>) vacation).get("used");
        assertEquals(10, used);
              
        // employee resigns
        given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body("{}")
        .when()
            .post("/vacations/" + id + "/resigned")
        .then()
            .statusCode(200);
        
        given()
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
        .when()
            .get("/vacations")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
 // @formatter:on
}
