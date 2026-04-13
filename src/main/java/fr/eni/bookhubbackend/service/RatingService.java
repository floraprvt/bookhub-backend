package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
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

    public Rating findRatingById(final Long id) {
        return ratingRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));
    }

    public void updateRating(final Long id,Rating rating, String email) {
        User ratingUSer = userRepository.findById(rating.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean isOwner = ratingUSer.getEmail().equals(email);

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this rating");
        }

        rating.setId(id);
        ratingRepository.save(rating);
    }

    public void deleteRating(final Long id, String email) {
        boolean isLibrarian = userRepository.findByEmail(email)
                .map(user -> user.getRole() == RoleEnum.LIBRARIAN || user.getRole() == RoleEnum.ADMIN)
                .orElse(false);

        if (!isLibrarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this rating");
        }

        ratingRepository.deleteById(id);
    }
}
