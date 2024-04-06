package gr.aegean.bookclientspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;

import gr.aegean.bookclientspring.client.MyRestClient;
import gr.aegean.bookclientspring.configuration.ImmutableApiConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(ImmutableApiConfiguration.class)
public class BookClientSpringApplication implements CommandLineRunner{
	
	@Autowired
	private MyRestClient mrc;

	public static void main(String[] args) {
		SpringApplication.run(BookClientSpringApplication.class, args);
	}
	
	@Override
	public void run(String... args) {
		MediaType xml = MediaType.APPLICATION_XML;
    	MediaType json = MediaType.APPLICATION_JSON;
    	String isbn1 = "978-3-16-148410-0";
    	String isbn2 = "978-3-16-148410-1";
    	//Adding two books
    	mrc.addBook(isbn1,json);
    	mrc.addBook(isbn2,json);
    	mrc.addBook(isbn1,json);
    	
    	//Getting all books in different media types
    	mrc.getBooks(xml);
    	mrc.getBooks(json);
    	
    	//Getting filtered books in different media types
    	mrc.getBooksWithParams("Title1",null,xml);
    	mrc.getBooksWithParams(null,"Publisher2",json);
    	
    	//Getting one book in different media types
    	mrc.getBook(isbn1,json);
    	mrc.getBook(isbn1,json);
    	
    	//Updating one book & checking the update
    	mrc.updateBook(isbn1,json);
    	mrc.getBook(isbn1,json);
    	
    	//Deleting first book & checking the deletion
    	mrc.deleteBook(isbn1);
    	mrc.deleteBook(isbn1);
    	mrc.getBooks(json);
	}

}
