package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.entity.dto.Search;
import fr.eni.bookhubbackend.mapper.BookMapper;
import fr.eni.bookhubbackend.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static fr.eni.bookhubbackend.ErrorKeys.BOOK_NOT_FOUND;

@AllArgsConstructor
@Service
public class BookService {

    private BookRepository bookRepository;
    private BookMapper bookMapper;


    public BookDto findBookById(final Long id) {

        return bookMapper.toBookDto(bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, BOOK_NOT_FOUND)));
    }

    public Page<BookDto> findAllBooks(final Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toBookDto);
    }

    public Page<BookDto> searchBooks(final Search search, final Pageable pageable) {
        return bookRepository.searchBook(search, pageable).map(bookMapper::toBookDto);
    }
}
