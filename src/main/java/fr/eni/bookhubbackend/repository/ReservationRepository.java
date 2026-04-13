package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Reservation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation,Long> {

    List<Reservation> findAllReservationsByUserId(final Long idUser);

    Boolean existsByUserIdAndBookId(final Long userId, final Long bookId);
    void deleteByUserIdAndBookId(final Long userId, final Long bookId);

}
