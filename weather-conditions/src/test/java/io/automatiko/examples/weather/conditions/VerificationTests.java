package io.automatiko.examples.weather.conditions;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(WiremockServices.class)
public class VerificationTests {
 // @formatter:off
    
    @BeforeAll
    public static void configure() {
        System.setProperty("IPSTACK_API_KEY", "abc");
        System.setProperty("OPEN_WEATHER_API_KEY", "xyz");
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testProcessExecution() {
        String payload = "{\n"
                + "  \"ip\": \"1.1.1.1\"\n"
                + "}";
        String id = "home";
        Map data = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
        .when()
            .post("/weatherConditions?businessKey=" + id)
        .then().log().body()
            .statusCode(200)        
            .body("id", equalTo(id)).extract().as(Map.class);

        Object location = data.get("location");
        Object forecast = data.get("forecast");
        
        assertNotNull(location);
        assertNotNull(forecast);        
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/weatherConditions")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testProcessExecutionEmptyLocation() {
        String payload = "{\n"
                + "  \"ip\": \"2.2.2.2\"\n"
                + "}";
        String id = "home";
        Map data = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
        .when()
            .post("/weatherConditions?businessKey=" + id)
        .then().log().body()
            .statusCode(200)        
            .body("id", equalTo(id)).extract().as(Map.class);

        Object location = data.get("location");
        Object forecast = data.get("forecast");
        
        assertNotNull(location);
        assertNull(forecast);   
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/weatherConditions")
        .then().statusCode(200)
            .body("$.size()", is(1));

        List<Map<String, String>> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get("/weatherConditions/" + id + "/tasks")
            .then()
                
                .statusCode(200)
                .extract().as(List.class);

        assertEquals(1, taskInfo.size());
        
        String taskId = taskInfo.get(0).get("id");
        String taskName = taskInfo.get(0).get("name");
        
        assertEquals("manualLocation", taskName);
        
        payload = "{\"location\" : {\"ip\":\"134.201.250.155\",\"type\":\"ipv4\",\"continent_code\":\"NA\",\"continent_name\":\"North America\",\"country_code\":\"US\",\"country_name\":\"United States\",\"region_code\":\"CA\",\"region_name\":\"California\",\"city\":\"Los Angeles\",\"zip\":\"90012\",\"latitude\":34.0655517578125,\"longitude\":-118.24053955078125,\"location\":{\"geoname_id\":5368361,\"capital\":\"Washington D.C.\",\"languages\":[{\"code\":\"en\",\"name\":\"English\",\"native\":\"English\"}],\"country_flag\":\"http:\\/\\/assets.ipstack.com\\/flags\\/us.svg\",\"country_flag_emoji\":\"\\ud83c\\uddfa\\ud83c\\uddf8\",\"country_flag_emoji_unicode\":\"U+1F1FA U+1F1F8\",\"calling_code\":\"1\",\"is_eu\":false}}}";
        data = given().
            contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(payload)
        .when()
            .post("/weatherConditions/" + id + "/" + taskName + "/" + taskId)
        .then()
            .statusCode(200).body("id", is(id)).extract().as(Map.class);  
        
        location = data.get("location");
        forecast = data.get("forecast");
        
        assertNotNull(location);
        assertNotNull(forecast);
        
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/weatherConditions")
        .then().statusCode(200)
            .body("$.size()", is(0));
    }
    
 // @formatter:on
}
