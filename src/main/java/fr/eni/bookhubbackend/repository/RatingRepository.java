package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Rating;
import fr.eni.bookhubbackend.entity.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByBook_IdAndUser_Email(Long bookId, String email);
    void deleteAllByUser(User user);


}
