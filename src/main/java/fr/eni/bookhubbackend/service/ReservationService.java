package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Reservation;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.dto.CreateReservationDto;
import fr.eni.bookhubbackend.entity.dto.ReservationDto;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static fr.eni.bookhubbackend.ErrorKeys.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    public List<ReservationDto> findMyReservations(final Long idUser) {
        if (!reservationRepository.existsById(idUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        return reservationRepository.findReservationsWithRankByUser(idUser);
    }

    @Transactional
    public void createReservation(final User user, final CreateReservationDto createReservationDto) {

        if (reservationRepository.countByUserId(user.getId()) >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NUMBER_MAXIMUM_RESERVATION);
        }

        Book book = bookRepository.findById(createReservationDto.bookId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, BOOK_NOT_FOUND));

        if (book.getIsAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BOOK_IS_AVAILABLE);
        }

        Reservation reservation = Reservation.builder()
                .book(book)
                .user(user)
                .date(LocalDateTime.now()).build();
        reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteMyReservation(final Long userId, final Long bookId) {
        if (!reservationRepository.existsByUserIdAndBookId(bookId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, RESERVATION_NOT_FOUND);
        }

        reservationRepository.deleteByUserIdAndBookId(bookId, userId);
    }
}
