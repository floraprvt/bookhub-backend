package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.mapper.BookMapper;
import fr.eni.bookhubbackend.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static fr.eni.bookhubbackend.ErrorKeys.BOOK_NOT_FOUND;

@AllArgsConstructor
@Service
public class BookService {

    private BookRepository bookRepository;
    private BookMapper bookMapper;


    public BookDto findBookById(final Long id) {

        return bookMapper.toBookDto(bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND)));
    }
}
