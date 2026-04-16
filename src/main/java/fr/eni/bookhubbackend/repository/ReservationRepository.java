package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Reservation;
import fr.eni.bookhubbackend.entity.dto.ReservationDto;
import org.springframework.data.jpa.repository.Query;
import fr.eni.bookhubbackend.entity.bo.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends CrudRepository<Reservation,Long> {


    @Query(value = """
             SELECT *
            FROM (
                SELECT
                    r.id as id,
                    r.user_id as userId,
                    b.id as bookId,
                    b.title as bookTitle,
                    b.image as bookImage,
                    r.date as date,
                    ROW_NUMBER() OVER (
                        PARTITION BY r.book_id
                        ORDER BY r.date ASC
                    ) as rank
                FROM reservation r
                JOIN book b ON b.id = r.book_id
            ) sub
            WHERE sub.userId = :userId
""", nativeQuery = true)
    List<ReservationDto> findReservationsWithRankByUser(@Param("userId") final Long userId);

    Long countByUserId(final Long idUser);
    long countByUser(User user);
    void deleteAllByUser(User user);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    void deleteByUserAndBook(User user, Book book);
    Optional<Reservation> findFirstByBookOrderByDateAsc(Book book);

    Long user(User user);
}
