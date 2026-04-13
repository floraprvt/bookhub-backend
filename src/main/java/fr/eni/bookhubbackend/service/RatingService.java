package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.repository.RatingRepository;
import fr.eni.bookhubbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public void saveRating(Long bookId, Rating rating, String email) {
        if (ratingRepository.existsByBook_IdAndUser_Email(bookId, email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already rated this book");
        }

        rating.setId(null);
        ratingRepository.save(rating);
    }

    public void updateRating(final Long bookId,Rating rating, String email) {
        User ratingUSer = userRepository.findById(rating.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean isOwner = ratingUSer.getEmail().equals(email);

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this rating");
        }

        rating.setId(bookId);
        ratingRepository.save(rating);
    }

    public void deleteRating(final Long id) {
        ratingRepository.deleteById(id);
    }
}
