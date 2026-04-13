package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings", description = "Gestion des notations")
public class RatingController {

    private final RatingService ratingService;

    @PutMapping("{id}")
    @Operation(summary = "Changer un avis", description = "Permet à un utilisateur de changer un de ses avis.")
    public ResponseEntity<?> updateRating(@PathVariable final Long id, @RequestBody Rating rating, Principal principal) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
        }

        ratingService.updateRating(id, rating, principal.getName());
        return ResponseEntity.ok(rating);
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
