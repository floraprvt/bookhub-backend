package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Loan;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.enums.RoleEnum;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.repository.LoanRepository;
import fr.eni.bookhubbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException; // <-- L'import important ici
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Loan createLoan(String email, Long bookId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email not found."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));

        if (!book.isAvailable()) {
            throw new IllegalStateException("This book is not available.");
        }

        int activeLoans = loanRepository.countByUserAndIsReturnedFalse(user);
        if (activeLoans >= 3) {
            throw new IllegalStateException("The user has reached their maximum quota of 3 loans.");
        }

        boolean hasLateLoans = loanRepository.findAllByUser(user).stream()
                .anyMatch(l -> !l.getIsReturned()
                        && l.getReturnDate().isBefore(LocalDate.now()));

        if (hasLateLoans) {
            throw new IllegalStateException("You cannot borrow a new book while you have overdue loans");
        }

        Loan newLoan = Loan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(14))
                .isReturned(false)
                .build();

        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(newLoan);
    }

    @Transactional
    public Loan returnLoan(Long loanId, String email) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found."));

        boolean isOwner = loan.getUser().getEmail().equals(email);
        boolean isLibrarian = userRepository.findByEmail(email)
                .map(u -> u.getRole() == RoleEnum.LIBRARIAN || u.getRole() == RoleEnum.ADMIN)
                .orElse(false);

        if (!isOwner && !isLibrarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to return another user's loan");
        }

        if (loan.getIsReturned()) {
            throw new IllegalStateException("The loan has already been returned.");
        }

        loan.setIsReturned(true);
        loan.setReturnDate(LocalDate.now());
        loan.getBook().setAvailable(true);
        bookRepository.save(loan.getBook());
        return loanRepository.save(loan);
    }

    public Map<String, List<Loan>> getMyLoans(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        List<Loan> all = loanRepository.findAllByUser(user);

        return Map.of(
                "active", all.stream().filter(l -> !l.getIsReturned()).toList(),
                "history", all.stream().filter(l -> l.getIsReturned()).toList()
        );
    }

    public List<Loan> getAllLoans(){
        return loanRepository.findAll();
    }
}