package gr.aegean.book.service.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import gr.aegean.book.service.model.Book;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;

/**
 * Tests the Web application, by checking that the index page returns a code 200.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Deleting Book Testing")
public class ThirdIT implements TestLifecycleLogger{
	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeAll
	public void initialiseRestAssuredMockMvcWebApplicationContext() {
	    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
	}
	
	@Test
    @Order(1)
    public void deleteWrongBook() throws Exception{
    	given().delete("/api/books/978-3-16-148410-5").then().assertThat().statusCode(404);
    }
    
    @Test
    @Order(2)
    public void deleteCorrectBook() throws Exception{
    	given().delete("/api/books/978-3-16-148410-0").then().assertThat().statusCode(204);
    }
    
    @Test
    @Order(3)
    public void getNonExistingBook() throws Exception{
    	given().accept("application/json").get("/api/books/978-3-16-148410-0").then().assertThat().statusCode(404);
    }
    
    @Test
    @Order(4)
    public void getExistingBooks() throws Exception{
    	List<Book> books = given().accept("application/json").get("/api/books").then().assertThat().
    			statusCode(200).extract().as(new TypeRef<List<Book>>(){});
    	assertThat(books, hasSize(3));
    	assertThat(books.get(0).getIsbn(),anyOf(equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
    	assertThat(books.get(1).getIsbn(),anyOf(equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
    	assertThat(books.get(2).getIsbn(),anyOf(equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
    }
}
