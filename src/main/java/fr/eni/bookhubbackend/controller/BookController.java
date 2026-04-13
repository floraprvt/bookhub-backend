package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.entity.dto.Search;
import fr.eni.bookhubbackend.service.BookService;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import fr.eni.bookhubbackend.entity.bo.Book;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("{id}")
    public ResponseEntity<BookDto> findBookById(@PathVariable final Long id) {
        return ResponseEntity.ok(bookService.findBookById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> findAllBooks(final Pageable pageable) {
        return ResponseEntity.ok(bookService.findAllBooks(pageable));
    }

    @GetMapping("search")
    public ResponseEntity<Page<BookDto>> searchBooks(@ParameterObject final Search search,  @ParameterObject
    @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(search, pageable));
    }

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        try {
            if (book != null && book.getId() == null) {
                bookService.addBook(book);
                return ResponseEntity.status(HttpStatus.CREATED).body(book);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book is mandatory, do not provide an id");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateBook(@Valid @RequestBody Book book) {
        try {
            if (book == null || book.getId() == null || book.getId() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book is mandatory, provide a valid id");
            }

            bookService.updateBook(book);
            return ResponseEntity.ok(book);
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        try {
            final long idBook = Long.parseLong(id);

            if (idBook <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
            }

            bookService.deleteBook(idBook);
            return ResponseEntity.ok("Book with id " + id + " has been deleted");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
        }
    }
}
