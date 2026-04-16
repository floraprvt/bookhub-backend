package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.dto.BookDto;
import fr.eni.bookhubbackend.entity.dto.RatingDto;
import fr.eni.bookhubbackend.entity.dto.Search;
import fr.eni.bookhubbackend.mapper.BookMapper;
import fr.eni.bookhubbackend.mapper.RatingMapper;
import fr.eni.bookhubbackend.service.BookService;
import fr.eni.bookhubbackend.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import fr.eni.bookhubbackend.entity.bo.Book;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Gestion des livres  ")
public class BookController {

    private final BookService bookService;
    private final RatingService ratingService;
    private final RatingMapper ratingMapper;
    private final BookMapper bookMapper;

    /**
     * Récupère un livre par son identifiant.
     * GET /api/books/{id}
     * Accessible à tous les utilisateurs authentifiés.
     *
     * @param id identifiant du livre
     * @return 200 avec le {@link BookDto} correspondant, ou 404 si introuvable
     */
    @GetMapping("{id}")
    public ResponseEntity<BookDto> findBookById(@PathVariable final Long id) {
        return ResponseEntity.ok(bookService.findBookById(id));
    }

    /**
     * Récupère la liste paginée de tous les livres, triés par titre par défaut.
     * GET /api/books?page=0&size=20&sort=title,asc
     * Accessible à tous les utilisateurs authentifiés.
     *
     * @param pageable paramètres de pagination et de tri (défaut : 20 par page, tri par titre ASC)
     * @return 200 avec une {@link Page} de {@link BookDto}
     */
    @GetMapping
    public ResponseEntity<Page<BookDto>> findAllBooks(@ParameterObject
                                                          @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok(bookService.findAllBooks(pageable));
    }

    /**
     * Recherche des livres selon des critères optionnels combinables (titre, ISBN, auteur, catégorie, date, disponibilité).
     * GET /api/books/search?title=...&isbn=...&authors=1,2&categoryList=3&isAvailable=true&page=0&size=20
     * Accessible à tous les utilisateurs authentifiés.
     *
     * @param search   critères de recherche (tous optionnels)
     * @param pageable paramètres de pagination et de tri
     * @return 200 avec une {@link Page} de {@link BookDto} correspondant aux critères
     */
    @GetMapping("search")
    public ResponseEntity<Page<BookDto>> searchBooks(@ParameterObject final Search search, @ParameterObject
    @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(search, pageable));
    }

    /**
     * Crée un nouveau livre dans le catalogue.
     * POST /api/books
     * Réservé aux rôles LIBRARIAN et ADMIN. Le corps de la requête ne doit pas contenir d'id.
     *
     * @param book données du livre à créer (sans id)
     * @return 201 avec le {@link BookDto} créé, ou 400 si le corps est invalide ou contient un id
     */
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter un livre", description = "Réservé au bibliothécaire. Ajoute un nouveau livre.")
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        if (book != null && book.getId() == null) {
            Book returnedBook = bookService.addBook(book);
            BookDto response = bookMapper.toBookDto(returnedBook);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le livre est obligatoire, ne donnez pas d'id");
        }
    }

    /**
     * Met à jour un livre existant dans le catalogue.
     * PUT /api/books
     * Réservé aux rôles LIBRARIAN et ADMIN. Le corps de la requête doit contenir un id valide (> 0).
     *
     * @param book données du livre à mettre à jour (avec id)
     * @return 200 avec le {@link BookDto} mis à jour, ou 400 si l'id est absent ou invalide
     */
    @PutMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un livre", description = "Réservé au bibliothécaire. Met à jour un livre existant.")
    public ResponseEntity<?> updateBook(@Valid @RequestBody Book book) {
        if (book == null || book.getId() == null || book.getId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le livre est obligatoire, donnez un id valide");
        }

        Book returnedBook = bookService.updateBook(book);
        BookDto response = bookMapper.toBookDto(returnedBook);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprime un livre du catalogue par son identifiant.
     * DELETE /api/books/{id}
     * Réservé au rôle ADMIN uniquement.
     *
     * @param id identifiant du livre à supprimer (doit être > 0)
     * @return 200 avec un message de confirmation, ou 400 si l'id est invalide
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un livre", description = "Réservé à l'administrateur. Supprime un livre.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Donnez un id valide");
        }

        bookService.deleteBook(id);
        return ResponseEntity.ok("Le livre avec l'id " + id + " a bien été supprimé");
    }

    /**
     * Ajoute un avis (note et commentaire) sur un livre pour l'utilisateur connecté.
     * POST /api/books/{id}/ratings
     * Accessible à tous les utilisateurs authentifiés. Un utilisateur ne peut laisser qu'un seul avis par livre.
     *
     * @param id        identifiant du livre à noter (doit être > 0)
     * @param rating    avis à enregistrer (note, commentaire)
     * @param principal utilisateur connecté extrait du JWT
     * @return 200 avec le {@link RatingDto} créé, ou 400 si l'id est invalide
     */
    @PostMapping("{id}/ratings")
    @Operation(summary = "Ajouter un avis", description = "Ajoute l'avis de l'utilisateur sur un livre.")
    public ResponseEntity<?> addBookRating(@PathVariable final Long id, @RequestBody Rating rating, Principal principal) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Donnez un id valide");
        }

        Rating returnedRating = ratingService.saveRating(id, rating, principal.getName());
        RatingDto response = ratingMapper.toRatingDto(returnedRating);
        return ResponseEntity.ok(response);
    }
}
