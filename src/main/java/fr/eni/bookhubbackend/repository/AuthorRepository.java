package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
