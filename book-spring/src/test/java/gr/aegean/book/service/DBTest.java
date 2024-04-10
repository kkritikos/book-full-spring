package gr.aegean.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import gr.aegean.book.service.model.Book;
import gr.aegean.book.service.repository.BookRepository;

@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
@DataJpaTest
public class DBTest implements TestInterface{
	/* Checking multiple positive cases for book creation through its Builder interface
	 * Along with the Storage & Retrieval from DB
	 */
	
	@Autowired
	BookRepository repo;
	
	@ParameterizedTest
	@Order(5)
	@CsvFileSource(resources="/positiveSingleBook.csv")
	void checkPositiveSingleBookWithDB(ArgumentsAccessor accessor) {
		Book book = BookUtility.createPositiveBook(accessor,0);
		repo.save(book);
		book = repo.findById(accessor.getString(0)).orElse(null);
		BookUtility.checkBook(book,accessor);
	}
	
	/* Checking book deletion positive cases
	 * 
	 */
	@ParameterizedTest
	@Order(2)
	@CsvSource({
		"ISBNNNN1, Title1, Author1, Publisher1",
		"ISBNNNN2, Title2, Author2, Publisher2",
		"ISBNNNN3, Title3, Author3, Publisher3",
	})
	void checkBookDeletion(String isbn, String title, String author, String publisher) {
		List<String> authors = Arrays.asList(author);
		Book book = new Book.Builder(isbn,title,authors,publisher).build();
		repo.save(book);
		repo.deleteById(isbn);
		book = repo.findById(isbn).orElse(null);
		assertNull(book);
	}
	
	//Checking book update on obligatory fields
	@ParameterizedTest
	@Order(4)
	@CsvSource({
		"ISBNNNN1, Title1, Author1, Publisher1",
		"ISBNNNN2, Title2, Author2, Publisher2",
		"ISBNNNN3, Title3, Author3, Publisher3",
	})
	void checkBookUpdate(String isbn, String title, String author, String publisher) {
		List<String> authors = Arrays.asList(author);
		Book book = new Book.Builder(isbn,title,authors,publisher).build();
		repo.save(book);
		Random r = new Random();
		int choice = r.nextInt(3);
		switch(choice) {
			case 0: book.setTitle("TITLE"); break;
			case 1: book.setAuthors(Arrays.asList("AUTHOR")); break;
			case 2: book.setPublisher("PUBLISHER"); break;
		}
		repo.save(book);
		book = repo.findById(isbn).orElse(null);
		assertNotNull(book);
		if (choice == 0) {
			assertEquals(book.getPublisher(),publisher);
			assertEquals(book.getAuthors().size(),1);
			assertEquals(book.getAuthors().get(0),author);
			assertEquals(book.getTitle(),"TITLE");
		}
		else if (choice == 1) {
			assertEquals(book.getPublisher(),publisher);
			assertEquals(book.getTitle(),title);
			assertEquals(book.getAuthors().size(),1);
			assertEquals(book.getAuthors().get(0),"AUTHOR");
		}
		else {
			assertEquals(book.getTitle(),title);
			assertEquals(book.getAuthors().size(),1);
			assertEquals(book.getAuthors().get(0),author);
			assertEquals(book.getPublisher(),"PUBLISHER");
		}
	}
	
	//Checking book retrieval (all books or some)
	@Test
	@Order(3)
	void checkBookRetrieval() {
		Book book1 = new Book.Builder("978-3-16-148410-0", "T1", Arrays.asList("Author1","Author2"), "P1").build();
		Book book2 = new Book.Builder("978-3-16-148410-1", "T2", Arrays.asList("Author2","Author3"), "P1").build();
		Book book3 = new Book.Builder("978-3-16-148410-2", "T3", Arrays.asList("Author3","Author1"), "P2").build();
		repo.save(book1);
		repo.save(book2);
		repo.save(book3);
		List<Book> books = repo.findAll();
		
		//Checking if we have 3 books and these are book1, book2 & book3
		assertEquals(books.size(),3);
		int matches = 0;
		for (Book book: books) {
			if (book.equals(book1) || book.equals(book2) || book.equals(book3)) {
				matches++;
			}
		}
		assertEquals(matches,3);
		
		//Checking that with 1 title, we get only one book
		books = repo.findByTitle("T1");
		assertEquals(books.size(),1);
		assertEquals(books.get(0),book1);
		books = repo.findByTitle("T2");
		assertEquals(books.size(),1);
		assertEquals(books.get(0),book2);
		
		//Checking that with one publisher, we get two books
		books = repo.findByPublisher("P1");
		assertEquals(books.size(),2);
		matches = 0;
		for (Book book: books) {
			if (book.equals(book1) || book.equals(book2)) {
				matches++;
			}
		}
		assertEquals(matches,2);
	}
}
