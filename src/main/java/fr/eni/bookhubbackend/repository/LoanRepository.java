package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Loan;
import fr.eni.bookhubbackend.entity.bo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    int countByUserAndIsReturnedFalse(User user);
    List<Loan> findAllByUser(User user);
    void deleteAllByUser(User user);
}
