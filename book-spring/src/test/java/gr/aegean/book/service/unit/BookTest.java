package gr.aegean.book.service.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import gr.aegean.book.service.model.Book;

public class BookTest implements TestInterface{
	
	//Checking multiple positive cases for book creation through its Builder interface
	@ParameterizedTest
	@CsvFileSource(resources="/positiveSingleBook.csv")
	void checkPositiveSingleBook(ArgumentsAccessor accessor) {
		Book book = BookUtility.createPositiveBook(accessor,0);
		BookUtility.checkBook(book,accessor);
	}
	
	//Checking multiple negative cases where obligatory book fields have null or empty or white space values
	@ParameterizedTest
	@CsvFileSource(resources="/negativeSingleBook.csv")
	void checkNegativeSingleBook(ArgumentsAccessor accessor) {
		Exception e = assertThrows(IllegalArgumentException.class, ()-> BookUtility.createNegativeBook(accessor));
		assertEquals(accessor.getString(6), e.getMessage());
	}
	
	//Checking that all fields of a book are null when its empty constructor is used
	@Test
	void checkEmptyConstructor() {
		Book book = new Book();
		assertNull(book.getIsbn());
		assertNull(book.getTitle());
		assertNull(book.getAuthors());
		assertNull(book.getCategory());
		assertNull(book.getPublisher());
		assertNull(book.getSummary());
		assertNull(book.getLanguage());
		assertNull(book.getDate());
	}
	
	//Checking multiple negative cases for wrong isbn values and whether the respective exception is thrown
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void nullEmptyAndBlankStringsforISBN(String isbn) {
		Book book = new Book();
		Exception e = assertThrows(IllegalArgumentException.class, ()-> book.setIsbn(isbn));
		assertEquals("ISBN cannot be null or empty", e.getMessage());
	}
	
	//Checking multiple positive cases for proper isbn values being set
	@ParameterizedTest
	@ValueSource(strings = { "ISBN1", "ISBN2", "I", "123" })
	void checkProperStringsforISBN(String isbn) {
		Book book = new Book();
		book.setIsbn(isbn);
		assertEquals(isbn,book.getIsbn());
	}
	
	//Checking multiple negative cases for wrong title values and whether the respective exception is thrown
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void nullEmptyAndBlankStringsforTitle(String title) {
		Book book = new Book();
		Exception e = assertThrows(IllegalArgumentException.class, ()-> book.setTitle(title));
		assertEquals("Title cannot be null or empty", e.getMessage());
	}
	
	//Checking multiple positive cases for proper title values being set
	@ParameterizedTest
	@ValueSource(strings = { "Title1", "Title2", "T", "Here is a title" })
	void checkProperStringsforTitle(String title) {
		Book book = new Book();
		book.setTitle(title);
		assertEquals(title,book.getTitle());
	}
	
	//Checking multiple negative cases for wrong publisher values and whether the respective exception is thrown
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void nullEmptyAndBlankStringsforPublisher(String publisher) {
		Book book = new Book();
		Exception e = assertThrows(IllegalArgumentException.class, ()-> book.setPublisher(publisher));
		assertEquals("Publisher cannot be null or empty", e.getMessage());
	}
	
	//Checking multiple positive cases for proper publisher values being set
	@ParameterizedTest
	@ValueSource(strings = { "P1", "Publisher2", "Publisher", "Publisher Name1" })
	void checkProperStringsforPublisher(String publisher) {
		Book book = new Book();
		book.setPublisher(publisher);
		assertEquals(publisher,book.getPublisher());
	}
	
	//Checking the 2 negative cases that the list of authors is null or empty and whether the respective exception is thrown
	@ParameterizedTest
	@MethodSource("negativeStringList")
	void nullEmptyListforAuthors(List<String> authors) {
		Book book = new Book();
		Exception e = assertThrows(IllegalArgumentException.class, ()-> book.setAuthors(authors));
		assertEquals("The list of authors cannot be null or empty", e.getMessage());
	}
	
	static Stream<List<String>> negativeStringList() {
		return Stream.of(null, new ArrayList<String>());
	}

	//Checking 3 positive cases for setting correct list of authors
	@ParameterizedTest
	@MethodSource("positiveStringList")
	void checkProperListforAuthors(List<String> authors) {
		Book book = new Book();
		book.setAuthors(authors);
		assertEquals(authors,book.getAuthors());
	}
	
	static Stream<List<String>> positiveStringList() {
		return Stream.of(Arrays.asList("Author1","Author2"), Arrays.asList("A1","A2"), Arrays.asList("Auth A1","Auth A2"));
	}
	
	//Checking negative values for category
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void checkNegativeValsforCategory(String category) {
		Book book = new Book();
		book.setCategory(category);
		assertNull(book.getCategory());
	}
	
	//Checking positive values for category
	@ParameterizedTest
	@ValueSource(strings = { "c", "Cat1", "Cat X", "C C C" })
	void checkPositiveValsforCategory(String category) {
		Book book = new Book();
		book.setCategory(category);
		assertEquals(book.getCategory(),category);
	}
	
	//Checking negative values for summary
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void checkNegativeValsforSummary(String summary) {
		Book book = new Book();
		book.setSummary(summary);
		assertNull(book.getSummary());
	}
		
	//Checking positive values for summary
	@ParameterizedTest
	@ValueSource(strings = { "s", "Sum1", "SUM X", "S X D" })
	void checkPositiveValsforSummary(String summary) {
		Book book = new Book();
		book.setSummary(summary);
		assertEquals(book.getSummary(),summary);
	}
	
	//Checking negative values for language
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void checkNegativeValsforLanguage(String language) {
		Book book = new Book();
		book.setLanguage(language);
		assertNull(book.getLanguage());
	}
			
	//Checking positive values for category
	@ParameterizedTest
	@ValueSource(strings = { "l", "Lang1", "Lang X", "L X D" })
	void checkPositiveValsforLanguage(String language) {
		Book book = new Book();
		book.setLanguage(language);
		assertEquals(book.getLanguage(),language);
	}
	
	//Checking negative values for date
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "   ", "\t", "\n" })
	void checkNegativeValsforDate(String date) {
		Book book = new Book();
		book.setDate(date);
		assertNull(book.getDate());
	}
				
	//Checking positive values for date
	@ParameterizedTest
	@ValueSource(strings = { "d", "Date", "12/12/1212", "22/12/2222" })
	void checkPositiveValsforDate(String date) {
		Book book = new Book();
		book.setDate(date);
		assertEquals(book.getDate(),date);
	}
}
