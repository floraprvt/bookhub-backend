package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static fr.eni.bookhubbackend.factory.BookFactory.createBook;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class BookServiceTest {

    @MockitoBean
    private BookRepository bookRepository; // mock du repository

    @Autowired
    private BookService bookService;


    @Test
    void findBookById_should_return_BookDto_when_book_found() {

        Book book = createBook();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        BookDto result = bookService.findBookById(book.getId());
        assertThat(result.title()).isEqualTo(book.getTitle());
        assertThat(result.date()).isEqualTo(book.getDate());
        assertThat(result.author().getFirst().id()).isEqualTo(book.getAuthor().getFirst().getId());
        assertThat(result.category().getFirst().id()).isEqualTo(book.getCategory().getFirst().getId());
    }
}