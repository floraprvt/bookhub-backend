package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings", description = "Gestion des notations")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("{id}")
    public ResponseEntity<Rating> findRatingById(@PathVariable final Long id) {
        return ResponseEntity.ok(ratingService.findRatingById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateRating(@PathVariable final Long id, @RequestBody Rating rating) {
        ratingService.updateRating(rating);
        return ResponseEntity.ok(rating);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteRating(@PathVariable final Long id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provide a valid id");
        }

        ratingService.deleteRating(id);
        return ResponseEntity.ok("Rating with id " + id + " has been deleted");
    }
}
