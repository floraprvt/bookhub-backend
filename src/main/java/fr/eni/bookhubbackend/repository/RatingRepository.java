package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
