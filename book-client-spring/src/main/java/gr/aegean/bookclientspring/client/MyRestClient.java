package gr.aegean.bookclientspring.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import gr.aegean.bookclientspring.configuration.ImmutableApiConfiguration;
import gr.aegean.book.model.Book;

@Component
public class MyRestClient {
	private final ImmutableApiConfiguration details;
	
	private RestClient client;
	
	private static int counter = 1;
	
	public MyRestClient(ImmutableApiConfiguration details){
		this.details = details;
		initClient();
	}
	
	private void initClient() {
		String url = "http://" + details.getHost() + ":" + details.getPort() + "/" + details.getApi();
		client = RestClient.create(url);
	}
	
	public void getBooks(MediaType type) {
		ResponseEntity<String> booksRep = client.get()
			.accept(type)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
				Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
				String message = s.hasNext() ? s.next() : "";
				System.out.println("Client error with HTTP Status Code: " + response.getStatusCode().value() +
						" and message: " + message);
			})
			.onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
				Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
				String message = s.hasNext() ? s.next() : "";
				System.out.println("Server error with HTTP Status Code: " + response.getStatusCode().value() +
						" and message: " + message);
			})
			.toEntity(String.class);
		int code = booksRep.getStatusCode().value(); 
		if (code < 300 && code >= 200) {
			System.out.println("The HTTP Status Code is: " + code);
			System.out.println("The list of books is: " + booksRep.getBody());
		}
	}
	
	public void getBooksWithParams(String title, String publisher, MediaType type) {
		String queryPart = "";
		if (title != null && !title.isBlank()) queryPart += "title=" + title;
		if (publisher != null && !publisher.isBlank()) queryPart += "publisher=" + publisher;
		if (!queryPart.isBlank()) queryPart = "?" + queryPart;
		ResponseEntity<String> booksRep = client.get()
			.uri(queryPart)
			.accept(type)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
				Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
				String message = s.hasNext() ? s.next() : "";
				System.out.println("Client error with HTTP Status Code: " + response.getStatusCode().value() +
						" and message: " + message);
			})
			.onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
				Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
				String message = s.hasNext() ? s.next() : "";
				System.out.println("Server error with HTTP Status Code: " + response.getStatusCode().value() +
						" and message: " + message);
			})
			.toEntity(String.class);
		int code = booksRep.getStatusCode().value(); 
		if (code < 300 && code >= 200) {
			System.out.println("The HTTP Status Code is: " + code);
			System.out.println("The list of books is: " + booksRep.getBody());
		}
	}
	
	public void getBook(String isbn, MediaType type) {
		ResponseEntity<String> bookRep = client.get()
			.uri("/{id}",isbn)
			.accept(type)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
				Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
				String message = s.hasNext() ? s.next() : "";
				System.out.println("Client error with HTTP Status Code: " + response.getStatusCode().value() +
						" and message: " + message);
			})
			.onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
				Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
				String message = s.hasNext() ? s.next() : "";
				System.out.println("Server error with HTTP Status Code: " + response.getStatusCode().value() +
						" and message: " + message);
			})
			.toEntity(String.class);
		int code = bookRep.getStatusCode().value(); 
		if (code < 300 && code >= 200) {
			System.out.println("The HTTP Status Code is: " + code);
			System.out.println("The book is: " + bookRep.getBody());
		}
	}
	
	private Book createBook(String isbn) {
    	Book book = new Book();
    	book.setIsbn(isbn);
    	book.setTitle("Title" + counter);
    	book.setPublisher("Publisher" + counter);
    	List<String> authors = new ArrayList<String>();
    	authors.add("Auth1");
    	authors.add("Auth2");
    	book.setAuthors(authors);
    	counter++;
    	
    	return book;
    }
	
	public void addBook(String isbn, MediaType type) {
		Book book = createBook(isbn);
		client.post()
			.contentType(type)
			.body(book)
			.exchange(
				(request, response) -> {
					if (response.getStatusCode().is4xxClientError()) {
						Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
						String message = s.hasNext() ? s.next() : "";
						System.out.println("Client error with HTTP Status Code: " + response.getStatusCode().value() +
									" and message: " + message);
					}
					else if (response.getStatusCode().is5xxServerError()) {
						Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
						String message = s.hasNext() ? s.next() : "";
						System.out.println("Server error with HTTP Status Code: " + response.getStatusCode().value() +
									" and message: " + message);
					}
					else if (response.getStatusCode().is2xxSuccessful()) {
						System.out.println("The HTTP Status Code is: " + response.getStatusCode().value());
						System.out.println("The book with isbn: " + isbn + " was created successfully");
						System.out.println("The created book's URL is: " + response.getHeaders().get("Location"));
					}
					return null;
				}
			);
	}
	
	public void updateBook(String isbn, MediaType type) {
		Book book = createBook(isbn);
		book.setTitle("Title3");
		client.put()
			.uri("/{id}",isbn)
			.contentType(type)
			.body(book)
			.exchange(
				(request, response) -> {
					if (response.getStatusCode().is4xxClientError()) {
						Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
						String message = s.hasNext() ? s.next() : "";
						System.out.println("Client error with HTTP Status Code: " + response.getStatusCode().value() +
								" and message: " + message);
					}
					else if (response.getStatusCode().is5xxServerError()) {
						Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
						String message = s.hasNext() ? s.next() : "";
						System.out.println("Server error with HTTP Status Code: " + response.getStatusCode().value() +
								" and message: " + message);
					}
					else if (response.getStatusCode().is2xxSuccessful()) {
						System.out.println("The HTTP Status Code is: " + response.getStatusCode().value());
						System.out.println("The book with isbn: " + isbn + " has been successfully updated");
					}
					return null;
				}
			);
	}
	
	public void deleteBook(String isbn) {
		client.delete()
			.uri("/{id}",isbn)
			.exchange(
				(request, response) -> {
					if (response.getStatusCode().is4xxClientError()) {
						Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
						String message = s.hasNext() ? s.next() : "";
						System.out.println("Client error with HTTP Status Code: " + response.getStatusCode().value() +
								" and message: " + message);
					}
					else if (response.getStatusCode().is5xxServerError()) {
						Scanner s = new Scanner(response.getBody()).useDelimiter("\\A");
						String message = s.hasNext() ? s.next() : "";
						System.out.println("Server error with HTTP Status Code: " + response.getStatusCode().value() +
								" and message: " + message);
					}
					else if (response.getStatusCode().is2xxSuccessful()) {
						System.out.println("The HTTP Status Code is: " + response.getStatusCode().value());
						System.out.println("The book with isbn: " + isbn + " has been successfully deleted");
					}
					return null;
				}
			);
	}
}
