package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
