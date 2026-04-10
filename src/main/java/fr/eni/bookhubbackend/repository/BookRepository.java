package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
