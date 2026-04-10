package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.entity.dto.Search;
import fr.eni.bookhubbackend.service.BookService;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/books")
public class BookController {

    private BookService bookService;


    @GetMapping("{id}")
    public ResponseEntity<BookDto> findBookById(@PathVariable final Long id) {
        return ResponseEntity.ok(bookService.findBookById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> findAllBooks(final Pageable pageable) {
        return ResponseEntity.ok(bookService.findAllBooks(pageable));
    }

    @GetMapping("search")
    public ResponseEntity<Page<BookDto>> searchBooks(@ParameterObject Search search, final Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(search, pageable));
    }
}
