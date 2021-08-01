package io.automatiko.examples.support.incidents;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(CassandraContainer.class)
public class VerificationTest {
 // @formatter:off
    
    @Test
    public void testProcessHappyPath() throws InterruptedException {
        
        String addPayload = "{\n"
                + "  \"incident\": {\n"
                + "    \"description\": \"A test of incident handling\",\n"
                + "    \"reporter\": {\n"
                + "      \"company\": \"automatiko\",\n"
                + "      \"email\": \"john@email.com\",\n"
                + "      \"name\": \"John Doe\"\n"
                + "    },\n"
                + "    \"severity\": \"normal\",\n"
                + "    \"title\": \"Verifying if all works well\"\n"
                + "  }\n"
                + "}";
        String id = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(addPayload)
            .when()
                .post("/v1_0/incidents")
            .then()
                //.log().body(true)
                .statusCode(200)
                        .body("id", notNullValue(), "status", equalTo("open"), "incident", notNullValue(), "comments", hasSize(0))
                        .extract().path("id");
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1_0/incidents")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\n"
                    + "  \"text\": \"here is a first comment\",\n"
                    + "  \"user\": \"john\"\n"
                    + "}")
        .when()
            .post("/v1_0/incidents/"+id +"/newcomment")
        .then()
            //.log().body(true)
            .statusCode(200)
            .body("id", notNullValue(), "status", equalTo("open"), "incident", notNullValue(), "comments", hasSize(1));
        

        given()
            .accept(ContentType.JSON)
        .when()
            .delete("/v1_0/incidents/" + id)
        .then().statusCode(200);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1_0/incidents")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @Test
    public void testProcessClosedByComment() throws InterruptedException {
        
        String addPayload = "{\n"
                + "  \"incident\": {\n"
                + "    \"description\": \"A test of incident handling\",\n"
                + "    \"reporter\": {\n"
                + "      \"company\": \"automatiko\",\n"
                + "      \"email\": \"john@email.com\",\n"
                + "      \"name\": \"John Doe\"\n"
                + "    },\n"
                + "    \"severity\": \"normal\",\n"
                + "    \"title\": \"Verifying if all works well\"\n"
                + "  }\n"
                + "}";
        String id = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(addPayload)
            .when()
                .post("/v1_0/incidents")
            .then()
                //.log().body(true)
                .statusCode(200)
                        .body("id", notNullValue(), "status", equalTo("open"), "incident", notNullValue(), "comments", hasSize(0))
                        .extract().path("id");
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1_0/incidents")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\n"
                    + "  \"text\": \"here is a first comment\",\n"
                    + "  \"user\": \"john\"\n"
                    + "}")
        .when()
            .post("/v1_0/incidents/"+id +"/newcomment")
        .then()
            //.log().body(true)
            .statusCode(200)
            .body("id", notNullValue(), "status", equalTo("open"), "incident", notNullValue(), "comments", hasSize(1));
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\n"
                    + "  \"text\": \"it works, case case be closed\",\n"
                    + "  \"user\": \"john\"\n"
                    + "}")
        .when()
            .post("/v1_0/incidents/"+id +"/newcomment")
        .then()
            //.log().body(true)
            .statusCode(200)
            .body("id", notNullValue(), "status", equalTo("closed"), "incident", notNullValue(), "comments", hasSize(2));       
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1_0/incidents")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testProcessCloseByTask() throws InterruptedException {
        
        String addPayload = "{\n"
                + "  \"incident\": {\n"
                + "    \"description\": \"A test of incident handling\",\n"
                + "    \"reporter\": {\n"
                + "      \"company\": \"automatiko\",\n"
                + "      \"email\": \"john@email.com\",\n"
                + "      \"name\": \"John Doe\"\n"
                + "    },\n"
                + "    \"severity\": \"normal\",\n"
                + "    \"title\": \"Verifying if all works well\"\n"
                + "  }\n"
                + "}";
        String id = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(addPayload)
            .when()
                .post("/v1_0/incidents")
            .then()
                //.log().body(true)
                .statusCode(200)
                        .body("id", notNullValue(), "status", equalTo("open"), "incident", notNullValue(), "comments", hasSize(0))
                        .extract().path("id");
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1_0/incidents")
        .then().statusCode(200)
            .body("$.size()", is(1));

        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/v1_0/incidents/"+id +"/tasks?group=first-line")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskId = taskInfo.get(0).get("id");
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("close", taskName);
        
        given().when()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{\"status\" : \"closed\"}")
            .post("/v1_0/incidents/"+id +"/close/" + taskId + "?group=first-line")
        .then()
            //.log().body(true)
            .statusCode(200)
            .body("id", notNullValue(), "status", equalTo("closed"), "incident", notNullValue(), "comments", hasSize(0));       
        
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/v1_0/incidents")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
 // @formatter:on
}
