package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static fr.eni.bookhubbackend.ErrorKeys.BOOK_NOT_FOUND;

@RequiredArgsConstructor
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

    public void addBook(Book book) {
        book.setIsAvailable(true);
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Long idBook) {
        bookRepository.deleteById(idBook);
    }
}
