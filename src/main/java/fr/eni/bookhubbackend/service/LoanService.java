package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Loan;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.DashboardStatsDto;
import fr.eni.bookhubbackend.entity.bo.dto.OverdueLoanDto;
import fr.eni.bookhubbackend.entity.bo.dto.TopBookDto;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.repository.LoanRepository;
import fr.eni.bookhubbackend.repository.ReservationRepository;
import fr.eni.bookhubbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    @Transactional
    public Loan createLoan(String email, Long bookId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email introuvable."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book introuvable."));

        if (!book.getIsAvailable()) {
            boolean isActivelyLoaned = loanRepository.existsActiveLoanByBookId(book.getId());
            if (isActivelyLoaned) {
                throw new IllegalStateException("Ce livre est actuellement emprunté.");
            }
            boolean isTopReservation = reservationRepository.findFirstByBookOrderByDateAsc(book)
                    .map(r -> r.getUser().getId().equals(user.getId()))
                    .orElse(false);
            if (!isTopReservation) {
                throw new IllegalStateException("Ce livre n'est pas disponible.");
            }
        }

        int activeLoans = loanRepository.countByUserAndIsReturnedFalse(user);
        if (activeLoans >= 3) {
            throw new IllegalStateException("Vous avez atteint votre quota de 3 réservations.");
        }

        boolean hasLateLoans = loanRepository.findAllByUser(user).stream()
                .anyMatch(l -> !l.getIsReturned()
                        && l.getReturnDate().isBefore(LocalDate.now()));

        if (hasLateLoans) {
            throw new IllegalStateException("Vous ne pouvez pas emprunter de nouveaux livres tant que vous avez des retards sur d'autres.");
        }

        Loan newLoan = Loan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(14))
                .isReturned(false)
                .build();

        book.setIsAvailable(false);
        bookRepository.save(book);
        reservationRepository.deleteByUserAndBook(user, book);

        return loanRepository.save(newLoan);
    }

    @Transactional
    public Loan returnLoan(Long loanId, String email) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Emprunt introuvable."));

        boolean isOwner = loan.getUser().getEmail().equals(email);
        boolean isLibrarian = userRepository.findByEmail(email)
                .map(u -> u.getRole() == RoleEnum.LIBRARIAN || u.getRole() == RoleEnum.ADMIN)
                .orElse(false);

        if (!isOwner && !isLibrarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vous ne pouvez pas retourner un emprunt d'une autre personne.");
        }

        if (loan.getIsReturned()) {
            throw new IllegalStateException("L'emprunt a déjà été retourné.");
        }

        loan.setIsReturned(true);
        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        boolean hasReservation = reservationRepository.findFirstByBookOrderByDateAsc(book)
                .map(reservation -> {
                    notificationService.createNotification(
                            reservation.getUser(),
                            "Le livre \"" + book.getTitle() + "\" est disponible pour vous ! Vous pouvez maintenant l'emprunter."
                    );
                    return true;
                })
                .orElse(false);

        if (!hasReservation) {
            book.setIsAvailable(true);
        }
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public Map<String, List<Loan>> getMyLoans(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        List<Loan> all = loanRepository.findAllByUser(user);

        return Map.of(
                "active", all.stream().filter(l -> !l.getIsReturned()).toList(),
                "history", all.stream().filter(l -> l.getIsReturned()).toList()
        );
    }

    public List<Loan> getAllLoans(){
        return loanRepository.findAll();
    }

    public DashboardStatsDto getStats() {
        long totalBooks = bookRepository.count();
        long activeLoans = loanRepository.countByIsReturnedFalse();
        long overdueLoans = loanRepository.countByIsReturnedFalseAndReturnDateBefore(LocalDate.now());
        return new DashboardStatsDto(totalBooks, activeLoans, overdueLoans);
    }

    public List<TopBookDto> getTop10MostBorrowed() {
        return loanRepository.findTop10MostBorrowed(PageRequest.of(0, 10));
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void notifyOverdueLoans() {
        List<Loan> overdueLoans = loanRepository
                .findByIsReturnedFalseAndReturnDateBeforeAndOverdueNotifiedFalse(LocalDate.now());

        System.out.println("[CRON] " + overdueLoans.size() + " emprunt(s) en retard trouvé(s)");

        for (Loan loan : overdueLoans) {
            notificationService.createNotification(
                    loan.getUser(),
                    "Votre emprunt du livre \"" + loan.getBook().getTitle() + "\" est en retard depuis le "
                            + loan.getReturnDate() + ". Merci de le retourner dès que possible."
            );
            loan.setOverdueNotified(true);
            loanRepository.save(loan);
        }
    }

    public void remindOverdueLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Emprunt introuvable."));

        if (loan.getIsReturned()) {
            throw new IllegalStateException("Cet emprunt a déjà été rendu.");
        }

        if (!loan.getReturnDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cet emprunt n'est pas en retard.");
        }

        notificationService.createNotification(
                loan.getUser(),
                "Rappel : votre emprunt du livre \"" + loan.getBook().getTitle() + "\" est en retard depuis le "
                        + loan.getReturnDate() + ". Merci de le retourner dès que possible."
        );
    }

    public List<OverdueLoanDto> getOverdueLoansRanked() {
        LocalDate today = LocalDate.now();
        return loanRepository.findByIsReturnedFalseAndReturnDateBeforeOrderByReturnDateAsc(today)
                .stream()
                .map(loan -> new OverdueLoanDto(
                        loan.getId(),
                        loan.getUser().getId(),
                        loan.getUser().getFirstName(),
                        loan.getUser().getLastName(),
                        loan.getBook().getTitle(),
                        loan.getReturnDate(),
                        ChronoUnit.DAYS.between(loan.getReturnDate(), today)
                ))
                .toList();
    }
}