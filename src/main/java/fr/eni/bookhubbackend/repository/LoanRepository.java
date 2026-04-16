package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Loan;
import fr.eni.bookhubbackend.entity.bo.User;
import fr.eni.bookhubbackend.entity.bo.dto.TopBookDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    int countByUserAndIsReturnedFalse(User user);
    long countByIsReturnedFalse();
    long countByIsReturnedFalseAndReturnDateBefore(LocalDate date);
    List<Loan> findAllByUser(User user);
    void deleteAllByUser(User user);

    @Query("SELECT new fr.eni.bookhubbackend.entity.bo.dto.TopBookDto(l.book.id, l.book.title, COUNT(l)) " +
           "FROM Loan l GROUP BY l.book.id, l.book.title ORDER BY COUNT(l) DESC")
    List<TopBookDto> findTop10MostBorrowed(Pageable pageable);

    List<Loan> findByIsReturnedFalseAndReturnDateBeforeOrderByReturnDateAsc(LocalDate date);
    @Query("SELECT l FROM Loan l WHERE l.isReturned = false AND l.returnDate < :date AND (l.overdueNotified = false OR l.overdueNotified IS NULL)")
    List<Loan> findByIsReturnedFalseAndReturnDateBeforeAndOverdueNotifiedFalse(@Param("date") LocalDate date);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.isReturned = false")
    boolean existsActiveLoanByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Loan l WHERE l.book.id = :bookId AND l.isReturned = false")
    boolean existsActiveLoanByBookId(@Param("bookId") Long bookId);
}
