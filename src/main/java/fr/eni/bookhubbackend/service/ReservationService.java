package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Reservation;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.dto.CreateReservationDto;
import fr.eni.bookhubbackend.entity.dto.ReservationDto;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.repository.LoanRepository;
import fr.eni.bookhubbackend.repository.ReservationRepository;
import fr.eni.bookhubbackend.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private static final int MAX_RESERVATIONS = 5;

    public List<ReservationDto> findMyReservations(final Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        return reservationRepository.findReservationsWithRankByUser(idUser);
    }

    @Transactional
    public void createReservation(final User user, final CreateReservationDto createReservationDto) {

        Book book = bookRepository.findById(createReservationDto.bookId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, BOOK_NOT_FOUND));

        if (reservationRepository.countByUserId(user.getId()) >= MAX_RESERVATIONS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NUMBER_MAXIMUM_RESERVATION);
        }

        if(reservationRepository.existsByUserIdAndBookId(user.getId(), createReservationDto.bookId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, RESERVATION_ALREADY_BOOKED);
        }

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
    public void deleteReservation(final User user, final Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, RESERVATION_NOT_FOUND));

        boolean isOwner = reservation.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals("ROLE_ADMIN");

        if(!isOwner && !isAdmin){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas autorisé à supprimé cette réservation");
        }

        reservationRepository.deleteById(id);
    }

    public boolean checkReservationExists(Long userId, Long bookId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, BOOK_NOT_FOUND));

        boolean hasReservation = reservationRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        boolean hasLoan = loanRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        return hasReservation || hasLoan;
    }
}
