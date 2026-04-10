package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.mapper.BookMapper;
import fr.eni.bookhubbackend.repository.BookRepository;
import lombok.AllArgsConstructor;
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
}
