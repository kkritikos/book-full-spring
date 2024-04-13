package gr.aegean.book.service.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.context.WebApplicationContext;

import gr.aegean.book.service.model.Book;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests the Web application, by checking that the index page returns a code 200.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DisplayName("Adding Book Testing")
@TestMethodOrder(OrderAnnotation.class)
public class SecondIT implements TestLifecycleLogger{
	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeAll
	public void initialiseRestAssuredMockMvcWebApplicationContext() {
	    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
	}
	
	 @Test
	    @Order(1)
	    public void addWrongBook() throws Exception{
	    	Book book = new Book();
	    	given().contentType("application/json").body(book).when().post("/api/books").then().assertThat().statusCode(400);
	    }
	    
	    private Book createBook(String isbn) {
	    	Random r = new Random();
	    	Book book = new Book();
	    	book.setIsbn(isbn);
	    	int pubId = r.nextInt(10) + 1;
	    	book.setPublisher("Pub" + pubId);
	    	int titleId = r.nextInt(10) + 1;
	    	book.setTitle("Title" + titleId);
	    	List<String> authors = new ArrayList<String>();
	    	int auth1 = r.nextInt(10) + 1;
	    	int auth2 = r.nextInt(10) + 1;
	    	authors.add("Auth" + auth1);
	    	authors.add("Auth2" + auth2);
	    	book.setAuthors(authors);
	    	
	    	return book;
	    }
	    
	    @Test
	    @Order(2)
	    public void postCorrectBook() throws Exception{
	    	Book book = createBook("978-3-16-148410-0");
	    	given().contentType("application/json").body(book).when().post("/api/books").then().assertThat().statusCode(201);
	    }
	    
	    @Test
	    @Order(3)
	    public void getExistingBook() throws Exception{
	    	given().accept("application/json").get("/api/books/978-3-16-148410-0").then().
	    	assertThat().statusCode(200).and().body("isbn", equalTo("978-3-16-148410-0"));
	    }
	    
	    @ParameterizedTest
	    @ValueSource(strings = { "978-3-16-148410-1", "978-3-16-148410-2", "978-3-16-148410-3" })
	    @Order(4)
	    void addCorrectBook(String isbn) {
	    	Book book = createBook(isbn);
	    	given().contentType("application/json").body(book).when().post("/api/books").then().assertThat().statusCode(201);
	    }
	    
	    @Test
	    @Order(5)
	    public void getExistingBooks() throws Exception{
	    	List<Book> books = given().accept("application/json").get("/api/books").then().assertThat().statusCode(200).
	    			extract().as(new TypeRef<List<Book>>(){});
	    	assertThat(books, hasSize(4));
	    	assertThat(books.get(0).getIsbn(),anyOf(equalTo("978-3-16-148410-0"),equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
	    	assertThat(books.get(1).getIsbn(),anyOf(equalTo("978-3-16-148410-0"),equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
	    	assertThat(books.get(2).getIsbn(),anyOf(equalTo("978-3-16-148410-0"),equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
	    	assertThat(books.get(3).getIsbn(),anyOf(equalTo("978-3-16-148410-0"),equalTo("978-3-16-148410-1"),equalTo("978-3-16-148410-2"),equalTo("978-3-16-148410-3")));
	    }
	    
	    @ParameterizedTest
	    @Order(6)
	    @ValueSource(strings = {"11", "12", "15", "19"})
	    public void useWrongPublisher(String publisherId) {
	    	given().accept("application/json").param("publisher","Publisher" + publisherId).get("/api/books").then().
	    	assertThat().statusCode(200).body(equalTo("[]"));
	    }    
}
