package gr.aegean.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import gr.aegean.book.service.model.Book;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class BookValidTest implements TestInterface{
	private static Validator validator = null;
	
	@BeforeAll
	static void constructValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	private static List<String> getMessages(Set<ConstraintViolation<Book>> viols){
		List<String> messages = new ArrayList<String>();
		for (ConstraintViolation<Book> viol: viols) {
			messages.add(viol.getMessage());
		}
		return messages;
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "ISBN1", "ISBN2", "I", "123" })
	void checkInvalidISBN(String isbn) {
		Book book = new Book();
		book.setIsbn(isbn);
		assertEquals(isbn,book.getIsbn());
		
		Set<ConstraintViolation<Book>> viols = validator.validate(book);
		assertNotEquals(viols.size(), 0);
		
		List<String> messages = getMessages(viols);
		assertTrue(messages.contains("isbn is invalid!"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "978-3-16-148410-0", "978-3-16-148410-1", "978-3-16-148410-2"})
	void checkValidISBN(String isbn) {
		Book book = new Book();
		book.setIsbn(isbn);
		assertEquals(isbn,book.getIsbn());
		
		Set<ConstraintViolation<Book>> viols = validator.validate(book);
		assertNotEquals(viols.size(), 0);
		
		List<String> messages = getMessages(viols);
		assertFalse(messages.contains("isbn is invalid!"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "*Book", "^Book", "Book&", "Book%2" })
	void checkInvalidTitle(String title) {
		Book book = new Book();
		book.setTitle(title);
		assertEquals(title,book.getTitle());
		
		Set<ConstraintViolation<Book>> viols = validator.validate(book);
		assertNotEquals(viols.size(), 0);
		
		List<String> messages = getMessages(viols);
		assertTrue(messages.contains("title is invalid!"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "My Java Book", "My Spring Boot Book", "My REST Book"})
	void checkValidTitle(String title) {
		Book book = new Book();
		book.setTitle(title);
		assertEquals(title,book.getTitle());
		
		Set<ConstraintViolation<Book>> viols = validator.validate(book);
		assertNotEquals(viols.size(), 0);
		
		List<String> messages = getMessages(viols);
		assertFalse(messages.contains("title is invalid!"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n", " \t \n" })
	void checkInvalidAuthorName(String author) {
		Book book = new Book();
		List<String> authors = new ArrayList<String>();
		authors.add(author);
		book.setAuthors(authors);
		assertIterableEquals(authors,book.getAuthors());
		
		Set<ConstraintViolation<Book>> viols = validator.validate(book);
		assertNotEquals(viols.size(), 0);
		
		List<String> messages = getMessages(viols);
		assertTrue(messages.contains("author name cannot be blank!"));
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "A", "Author1", "Author", "Author A" })
	void checkValidAuthorName(String author) {
		Book book = new Book();
		List<String> authors = new ArrayList<String>();
		authors.add(author);
		book.setAuthors(authors);
		assertIterableEquals(authors,book.getAuthors());
		
		Set<ConstraintViolation<Book>> viols = validator.validate(book);
		assertNotEquals(viols.size(), 0);
		
		List<String> messages = getMessages(viols);
		assertFalse(messages.contains("author name cannot be blank!"));
	}
}
