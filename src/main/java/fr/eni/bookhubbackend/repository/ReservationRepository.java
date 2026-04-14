package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Reservation;
import fr.eni.bookhubbackend.entity.dto.ReservationDto;
import org.springframework.data.jpa.repository.Query;
import fr.eni.bookhubbackend.entity.bo.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation,Long> {


    @Query(value = """
    SELECT 
           r.user_id as userId,
           r.book_id as bookId,
           r.date as date,
           ROW_NUMBER() OVER (
               PARTITION BY r.book_id
               ORDER BY r.date ASC
           ) as rank
    FROM reservation r
    WHERE r.user_id = :userId
""", nativeQuery = true)
    List<ReservationDto> findReservationsWithRankByUser(@Param("userId") final Long userId);

    Long countByUserId(final Long idUser);
    long countByUser(User user);
    void deleteAllByUser(User user);

}
