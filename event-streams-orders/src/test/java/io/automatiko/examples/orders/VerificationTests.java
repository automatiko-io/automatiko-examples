package io.automatiko.examples.orders;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.automatiko.engine.api.event.EventPublisher;
import io.automatiko.engine.services.event.impl.CountDownProcessInstanceEventPublisher;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@QuarkusTest
public class VerificationTests {
 // @formatter:off
    
    @Inject 
    @Any
    InMemoryConnector connector;
    
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testProcessExecution() throws Exception {
        String createdOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Created\"\n"
                + "  }\n"
                + "}";
        
        InMemorySource<KafkaRecord<String, String>> channelOrders = connector.source("orders");          
        
        String id = "ORDER-1";
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, createdOrder));
        execCounter.waitTillCompletion(5);
         
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = getOrderData(id);
        
        Object order = data.get("order");             
        assertNotNull(order);
        Map<String, Object> orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        Object items = data.get("items");             
        assertNotNull(items);
        
        String item = "{\n"
                + "  \"item\" : {\n"
                + "    \"articleId\" : \"1234\",\n"
                + "    \"name\" : \"pen\",\n"
                + "    \"price\" : 10,\n"
                + "    \"quantity\" : 4\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, item));
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        
        String placedOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Placed\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, placedOrder));
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Placed", orderAsMap.get("status"));
        assertEquals(36.0, orderAsMap.get("amount"));
        
        String shippedOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Shipped\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, shippedOrder));
        execCounter.waitTillCompletion(5);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testProcessExecutionCancelInsteadOfShip() throws Exception {
        String createdOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Created\"\n"
                + "  }\n"
                + "}";
        
        InMemorySource<KafkaRecord<String, String>> channelOrders = connector.source("orders");          
        
        String id = "ORDER-1";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, createdOrder));
        execCounter.waitTillCompletion(5);
         
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = getOrderData(id);
        
        Object order = data.get("order");             
        assertNotNull(order);
        Map<String, Object> orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        Object items = data.get("items");             
        assertNotNull(items);
        
        String item = "{\n"
                + "  \"item\" : {\n"
                + "    \"articleId\" : \"1234\",\n"
                + "    \"name\" : \"pen\",\n"
                + "    \"price\" : 10,\n"
                + "    \"quantity\" : 4\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, item));
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        
        String placedOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Placed\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, placedOrder));
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Placed", orderAsMap.get("status"));
        assertEquals(36.0, orderAsMap.get("amount"));
        
        String cancelledOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Cancelled\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, cancelledOrder));
        execCounter.waitTillCompletion(5);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testProcessExecutionCancelDirectly() throws Exception {
        String createdOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Created\"\n"
                + "  }\n"
                + "}";
        
        InMemorySource<KafkaRecord<String, String>> channelOrders = connector.source("orders");          
        
        String id = "ORDER-1";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, createdOrder));
        execCounter.waitTillCompletion(5);
         
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = getOrderData(id);
        
        Object order = data.get("order");             
        assertNotNull(order);
        Map<String, Object> orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        Object items = data.get("items");             
        assertNotNull(items);
        
        String cancelledOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Cancelled\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, cancelledOrder));
        execCounter.waitTillCompletion(5);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testProcessExecutionUpdatedCustomer() throws Exception {
        String createdOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Created\"\n"
                + "  }\n"
                + "}";
        
        InMemorySource<KafkaRecord<String, String>> channelOrders = connector.source("orders");
        InMemorySource<KafkaRecord<String, String>> channelCustomers = connector.source("customers");  
        
        String id = "ORDER-1";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, createdOrder));
        execCounter.waitTillCompletion(5);
         
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(1));
        
        Map data = getOrderData(id);
        
        Object order = data.get("order");             
        assertNotNull(order);
        Map<String, Object> orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        Object customer = orderAsMap.get("customer");             
        assertNotNull(customer);
        Map<String, Object> customerAsMap = (Map<String, Object>) customer;
        assertEquals("john@doe.org", customerAsMap.get("email"));
        Map<String, Object> addressAsMap = (Map<String, Object>) customerAsMap.get("address");
        assertEquals("Main Street 1", addressAsMap.get("street"));
        assertEquals("10000", addressAsMap.get("zipCode"));
        assertEquals("New York", addressAsMap.get("city"));
        assertEquals("US", addressAsMap.get("country"));
        
        Object items = data.get("items");             
        assertNotNull(items);
        
        String item = "{\n"
                + "  \"item\" : {\n"
                + "    \"articleId\" : \"1234\",\n"
                + "    \"name\" : \"pen\",\n"
                + "    \"price\" : 10,\n"
                + "    \"quantity\" : 4\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, item));  
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Created", orderAsMap.get("status"));
        assertEquals(0.0, orderAsMap.get("amount"));
        
        
        String placedOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Placed\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, placedOrder));
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Placed", orderAsMap.get("status"));
        assertEquals(36.0, orderAsMap.get("amount"));
        
        customer = orderAsMap.get("customer");             
        assertNotNull(customer);
        customerAsMap = (Map<String, Object>) customer;
        assertEquals("john@doe.org", customerAsMap.get("email"));
        addressAsMap = (Map<String, Object>) customerAsMap.get("address");
        assertEquals("Main Street 1", addressAsMap.get("street"));
        assertEquals("10000", addressAsMap.get("zipCode"));
        assertEquals("New York", addressAsMap.get("city"));
        assertEquals("US", addressAsMap.get("country"));
        
        String customerPayload = "{\n"
                + "    \"firstName\": \"John\",\n"
                + "    \"lastName\": \"Doe\",\n"
                + "    \"email\": \"john@doe.org\",\n"
                + "    \"phone\": \"123456\",\n"
                + "    \"address\": {\n"
                + "        \"street\": \"Second avenue 4\",\n"
                + "        \"city\": \"Boston\",\n"
                + "        \"zipCode\": \"00022\",\n"
                + "        \"country\": \"US\"\n"
                + "    }\n"
                + "}";
        
        execCounter.reset(1);
        channelCustomers.send(KafkaRecord.of(id, customerPayload));
        execCounter.waitTillCompletion(5);
        
        data = getOrderData(id);
        order = data.get("order");             
        assertNotNull(order);
        orderAsMap = (Map<String, Object>) order;
        assertEquals("ORDER-1", orderAsMap.get("orderNumber"));
        assertEquals("Placed", orderAsMap.get("status"));
        assertEquals(36.0, orderAsMap.get("amount"));
        
        customer = orderAsMap.get("customer");             
        assertNotNull(customer);
        customerAsMap = (Map<String, Object>) customer;
        assertEquals("john@doe.org", customerAsMap.get("email"));
        addressAsMap = (Map<String, Object>) customerAsMap.get("address");
        assertEquals("Second avenue 4", addressAsMap.get("street"));
        assertEquals("00022", addressAsMap.get("zipCode"));
        assertEquals("Boston", addressAsMap.get("city"));
        assertEquals("US", addressAsMap.get("country"));
        
        String shippedOrder = "{\n"
                + "  \"order\": {\n"
                + "    \"customer\": {\n"
                + "      \"address\": {\n"
                + "        \"city\": \"New York\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"street\": \"Main Street 1\",\n"
                + "        \"zipCode\": \"10000\"\n"
                + "      },\n"
                + "      \"email\": \"john@doe.org\",\n"
                + "      \"firstName\": \"John\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"phone\": \"123456\"\n"
                + "    },\n"
                + "    \"orderDate\": \"2020-12-07\",\n"
                + "    \"orderNumber\": \"ORDER-1\",\n"
                + "    \"status\": \"Shipped\"\n"
                + "  }\n"
                + "}";
        
        execCounter.reset(1);
        channelOrders.send(KafkaRecord.of(id, shippedOrder));
        execCounter.waitTillCompletion(5);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/orders")
        .then().statusCode(200)
            .body("$.size()", is(0));
        
    }
 // @formatter:on

    @SuppressWarnings("unchecked")
    private Map<String, Object> getOrderData(String id) {
        return given()
                .accept(ContentType.JSON)
                .when()
                .get("/orders/" + id)
                .then()
                .statusCode(200).body("id", is(id)).extract().as(Map.class);
    }
}
