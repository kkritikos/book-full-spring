package gr.aegean.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

import gr.aegean.book.service.model.Book;

public class BookUtility {
	//Creating a book from each CSV row
	public static Book createPositiveBook(ArgumentsAccessor accessor, int start) {
		String isbn = accessor.getString(start + 0);
		String title = accessor.getString(start + 1);
		String author1 = accessor.getString(start + 2);
		String author2 = accessor.getString(start + 3);
		List<String> authors = new ArrayList<String>();
		authors.add(author1);
		if (author2 != null && !author2.trim().equals("")) authors.add(author2);
		String publisher = accessor.getString(start + 4);
		String category = accessor.getString(start + 5);
		String summary = accessor.getString(start + 6);
		String language = accessor.getString(start + 7);
		String date = accessor.getString(start + 8);
		
		return new Book.Builder(isbn, title, authors, publisher).
				category(category).summary(summary).language(language).date(date).build();
	}
	
	//Trying to create a book from each CVS row - The creation of book will deliberately fail
	public static void createNegativeBook(ArgumentsAccessor accessor) {
		String isbn = accessor.getString(0);
		String title = accessor.getString(1);
		String author1 = accessor.getString(2);
		String author2 = accessor.getString(3);
		List<String> authors = new ArrayList<String>();
		if (author1 != null && !author1.trim().equals("")) authors.add(author1);
		if (author2 != null && !author2.trim().equals("")) authors.add(author2);
		Random r = new Random();
		if (r.nextInt(2) == 1) authors = null;
		String publisher = accessor.getString(4);
		String category = accessor.getString(5);
		
		Book book = new Book.Builder(isbn, title, authors, publisher).
				category(category).build();
		System.out.println("Got book: " + book);
	}
	
	//Checking if a created book from a CVS row has correct values
	public static void checkBook(Book book, ArgumentsAccessor accessor) {
		assertEquals(book.getIsbn(),accessor.get(0));
		assertEquals(book.getTitle(),accessor.get(1));
		
		String author1 = accessor.getString(2);
		String author2 = accessor.getString(3);
		List<String> authors = new ArrayList<String>();
		authors.add(author1);
		if (author2 != null && !author2.trim().equals("")) authors.add(author2);
		assertEquals(authors, book.getAuthors());
		
		assertEquals(book.getPublisher(),accessor.getString(4));
		assertEquals(book.getCategory(),accessor.getString(5));
		assertEquals(book.getSummary(),accessor.getString(6));
		assertEquals(book.getLanguage(),accessor.getString(7));
		assertEquals(book.getDate(),accessor.getString(8));
	}
}
