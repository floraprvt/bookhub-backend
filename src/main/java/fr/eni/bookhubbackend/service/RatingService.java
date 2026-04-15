package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.dto.RatingDto;
import fr.eni.bookhubbackend.mapper.RatingMapper;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.repository.RatingRepository;
import fr.eni.bookhubbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public List<RatingDto> getRatingsByBookId(Long bookId) {
        return ratingRepository.findByBook_Id(bookId).stream()
                .map(ratingMapper::toRatingDto)
                .toList();
    }

    public Rating saveRating(Long bookId, Rating rating, String email) {
        if (ratingRepository.existsByBook_IdAndUser_Email(bookId, email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already rated this book");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        rating.setId(null);
        rating.setUser(user);
        rating.setBook(book);
        return ratingRepository.save(rating);
    }

    public Rating updateRating(Long ratingId, Rating rating, String email) {

        User ratingUSer = userRepository.findById(rating.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean isOwner = ratingUSer.getEmail().equals(email);
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this rating");
        }
        Rating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        existingRating.setScore(rating.getScore());
        existingRating.setComment(rating.getComment());
        existingRating.setDate(rating.getDate());
        return ratingRepository.save(existingRating);
    }

    public void deleteRating(final Long id) {
        ratingRepository.deleteById(id);
    }
}
