package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBook(Book book) {
        book.setIsAvailable(true);

         try {
            return bookRepository.save(book);
         } catch (Exception e) {
             throw new IllegalArgumentException("Book could not be saved");
         }
    }

    public Book updateBook(Book book) {
        try {
            return bookRepository.save(book);
        }  catch (Exception e) {
            throw new IllegalArgumentException("Book could not be saved");
        }
    }

    public void deleteBook(Long idBook) {
        try {
            bookRepository.deleteById(idBook);
        } catch (Exception e) {
            throw new IllegalArgumentException("Book could not be deleted");
        }
    }
}
