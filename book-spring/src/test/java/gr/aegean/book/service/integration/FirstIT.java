package gr.aegean.book.service.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.*;

/**
 * Tests the Web application, by checking that the index page returns a code 200.
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("RestBook API Testing")
public class FirstIT implements TestLifecycleLogger{
    
    @Test
    @Order(1)
    public void callBooks() throws Exception
    {
        given().accept("application/json").get("/api/books").then().assertThat().contentType("application/json").and().statusCode(200);
    }
    
    @Test
    @Order(2)
    public void getBookWhileNoOneExists() throws Exception{
    	given().accept("application/json").get("/api/books/xxx").then().assertThat().statusCode(404);
    }    
}
