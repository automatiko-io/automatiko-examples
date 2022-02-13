package io.automatiko.examples.vacation.requests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;

@QuarkusTest
public class GraphQLVerificationTest {
    static {
        RestAssured.registerParser("application/json, application/javascript, text/javascript, text/json", Parser.JSON);

    }

    // @formatter:off
    @Test
    public void testVacationRequestHappyPath() {
        String payload = "{\"query\":\"mutation {"
                + "  create_vacations(data: {employee: { email: \\\"mary@email.com\\\", firstName: \\\"Mary\\\", lastName: \\\"Jane\\\", startedAt: \\\"2000-12-01T19:20+01:00\\\", department: \\\"finance\\\" }}) {"
                + "    id,"
                + "    employee {"
                + "      department,"
                + "      manager"
                + "    },"
                + "    vacation {"
                + "      eligible,"
                + "      used"
                + "    }"
                + "  }"
                + " }\""
                + "}";
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(payload)
            .when()
                .post("/graphql")
            .then()
                //.log().all(true)
                .statusCode(200)
                .body("data.create_vacations.id", notNullValue(), 
                        "data.create_vacations.employee.department", equalTo("finance"), 
                        "data.create_vacations.employee.manager", equalTo("john@email.com"), 
                        "data.create_vacations.vacation.eligible", equalTo(20), 
                        "data.create_vacations.vacation.used", equalTo(0));
        
        String getInstances = "{\"query\":\"query {get_all_vacations(user:\\\"mary@email.com\\\") {id}}\"}";
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(getInstances)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.get_all_vacations.size()", is(1));
        
        String submit = "{\"query\":\"mutation {"
                + "  signal_submit_0(id: \\\"mary@email.com\\\", user:\\\"mary@email.com\\\", model: {from: \\\"2021-11-01T00:00:00Z\\\", to: \\\"2021-11-03T00:00:00Z\\\", length: 3, key: \\\"travel\\\"}) {"
                + "    vacation {"
                + "      eligible,"
                + "      used"
                + "    }"
                + "  }"
                + " }\""
                + "}";
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(submit)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.signal_submit_0.vacation.used", equalTo(3));      
        
        String getTasks = "{\"query\":\"query {"
                + "  get_vacations_tasks(id:\\\"mary@email.com\\\", user:\\\"john@email.com\\\") {"
                + "    name,"
                + "    formLink,"
                + "    id"
                + "  }"
                + "}\"}";
        
        String approveTaskId = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("john@email.com", "john")
            .body(getTasks)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.get_vacations_tasks.size()", is(1))
            .extract().path("data.get_vacations_tasks[0].id");
        
        getTasks = "{\"query\":\"query {"
                + "  get_vacations_tasks(id:\\\"mary@email.com\\\", user:\\\"mary@email.com\\\") {"
                + "    name,"
                + "    formLink,"
                + "    id"
                + "  }"
                + "}\"}";
        
        String cancelTaskId = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(getTasks)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.get_vacations_tasks.size()", is(1))
            .extract().path("data.get_vacations_tasks[0].id");
        
        String approve = "{\"query\":\"mutation {"
                + "  vacations_completeTask_approval_requests_0(parentId:\\\"mary@email.com\\\", id:\\\"travel\\\", workItemId: \\\"" + approveTaskId + "\\\", user:\\\"john@email.com\\\", data: {approved:true}) {"
                + "    request {"
                + "      key,"
                + "      approved"
                + "    }"
                + "  }"
                + "}\"}";
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("john@email.com", "john")
            .body(approve)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.vacations_completeTask_approval_requests_0.request.approved", equalTo(true));  
        
        String cancel = "{\"query\":\"mutation {"
                + "  vacations_completeTask_cancel_requests_1(parentId:\\\"mary@email.com\\\", id:\\\"travel\\\", workItemId: \\\"" + cancelTaskId +"\\\", user:\\\"mary@email.com\\\", data: {reason:\\\"need to change my plans\\\"}) {"
                + "    request {"
                + "      key,"
                + "      approved,"
                + "      cancelled,"
                + "      comment"
                + "    }"
                + "  }"
                + "}\"}";
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(cancel)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.vacations_completeTask_cancel_requests_1.request.cancelled", equalTo(true));
        
        String abortInstance = "{\"query\":\"mutation {"
                + "  delete_vacations(id:\\\"mary@email.com\\\", user:\\\"mary@email.com\\\") {"
                + "    id,"
                + "    employee {"
                + "      department,"
                + "      manager"
                + "    },"
                + "    vacation {"
                + "      eligible,"
                + "      used"
                + "    }"
                + "  }"
                + " }\""
                + "}";
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(abortInstance)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.delete_vacations.vacation.used", equalTo(0));
        
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .auth().basic("mary@email.com", "mary")
            .body(getInstances)
        .when()
            .post("/graphql")
        .then()
            //.log().all(true)
            .statusCode(200)
            .body("data.get_all_vacations.size()", is(0));
    }
    
    // @formatter:on
}
