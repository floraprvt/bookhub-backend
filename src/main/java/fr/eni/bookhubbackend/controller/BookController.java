package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.service.BookService;
import fr.eni.bookhubbackend.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Gestion des livres  ")
public class BookController {

    private final BookService bookService;
    private final RatingService ratingService;

    @GetMapping("{id}")
    public ResponseEntity<BookDto> findBookById(@PathVariable final Long id) {
        return ResponseEntity.ok(bookService.findBookById(id));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
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

    @PostMapping("{id}/ratings")
    @Operation(summary = "Ajouter un avis", description = "Ajoute l'avis de l'utilisateur sur un livre.")
    public ResponseEntity<?> addBookRating(@PathVariable final Long id, @RequestBody Rating rating, Principal principal) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
        }

        ratingService.saveRating(id, rating, principal.getName());
        return ResponseEntity.ok(rating);
    }
}
