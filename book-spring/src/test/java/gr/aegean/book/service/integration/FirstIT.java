package gr.aegean.book.service.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.*;

/**
 * Tests the Web application, by checking that the index page returns a code 200.
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("RestBook API Testing")
public class FirstIT implements TestLifecycleLogger{
	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeAll
	public void initialiseRestAssuredMockMvcWebApplicationContext() {
	    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
	}
    
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
