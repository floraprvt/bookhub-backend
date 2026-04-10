package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.mapper.BookMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static fr.eni.bookhubbackend.ErrorKeys.BOOK_NOT_FOUND;

@AllArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private BookMapper bookMapper;

    public BookDto findBookById(final Long id) {
        return bookMapper.toBookDto(bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, BOOK_NOT_FOUND)));
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
