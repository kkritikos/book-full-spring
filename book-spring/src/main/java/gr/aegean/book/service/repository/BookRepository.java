package gr.aegean.book.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gr.aegean.book.service.model.Book;

public interface BookRepository extends JpaRepository<Book, String>{
	List<Book> findByTitle(String title);
	List<Book> findByPublisher(String publisher);
	List<Book> findByTitleAndPublisher(String title, String publisher);
}
