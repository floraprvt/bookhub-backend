package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public Rating findRatingById(final Long id) {
        return ratingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));
    }

    public void updateRating(final Long id, Rating rating, String email) {
        System.out.print("update in service with");
        System.out.print(rating.getUser().getEmail());
         boolean isOwner = rating.getUser().getEmail().equals(email);

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this rating");
        }

        rating.setId(id);
        ratingRepository.save(rating);
    }

    public void deleteRating(final Long id) {
         ratingRepository.deleteById(id);
    }
}
