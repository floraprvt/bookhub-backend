package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/books")
public class BookController {

    private BookService bookService;


    @GetMapping("{id}")
    public ResponseEntity<BookDto> findBookById(@PathVariable final Long id) {
        return ResponseEntity.ok(bookService.findBookById(id));
    }
}
