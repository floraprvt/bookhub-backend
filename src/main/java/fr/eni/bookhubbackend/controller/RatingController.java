package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.dto.RatingDto;
import fr.eni.bookhubbackend.mapper.RatingMapper;
import fr.eni.bookhubbackend.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings", description = "Gestion des notations")
public class RatingController {

    private final RatingService ratingService;
    private final RatingMapper ratingMapper;

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Lister les avis d'un livre", description = "Retourne tous les avis associés à un livre.")
    public ResponseEntity<List<RatingDto>> getRatingsByBookId(@PathVariable final Long bookId) {
        return ResponseEntity.ok(ratingService.getRatingsByBookId(bookId));
    }

    @PostMapping("/book/{bookId}")
    @Operation(summary = "Ajouter un avis", description = "Permet à un utilisateur d'ajouter un avis sur un livre.")
    public ResponseEntity<?> addRating(@PathVariable final Long bookId, @RequestBody Rating rating, Principal principal) {
        Rating saved = ratingService.saveRating(bookId, rating, principal.getName());
        RatingDto response = ratingMapper.toRatingDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    @Operation(summary = "Changer un avis", description = "Permet à un utilisateur de changer un de ses avis.")
    public ResponseEntity<?> updateRating(@PathVariable final Long id, @RequestBody Rating rating, Principal principal) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
        }

        Rating returnedRating = ratingService.updateRating(id, rating, principal.getName());
        RatingDto response = ratingMapper.toRatingDto(returnedRating);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un avis", description = "Réservé aux bibliothécaires. Supprime l'avis d'un utilisateur.")
    public ResponseEntity<?> deleteRating(@PathVariable final Long id, Principal principal) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
        }

        ratingService.deleteRating(id);
        return ResponseEntity.ok("Rating with id " + id + " has been deleted");
    }
}
