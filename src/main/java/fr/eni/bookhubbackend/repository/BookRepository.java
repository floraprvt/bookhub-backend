package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.dto.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book,Long> {

    Page<Book> findAll(Pageable pageable);


    @Query("""
SELECT DISTINCT b
FROM Book b
LEFT JOIN b.category c
LEFT JOIN b.author a
WHERE
(:#{#search.title} IS NULL
    OR LOWER(b.title) LIKE LOWER(CONCAT(:#{#search.title}, '%')))

AND (:#{#search.isbn} IS NULL
    OR b.isbn = :#{#search.isbn})

AND (:#{#search.date} IS NULL
    OR b.date = :#{#search.date})

AND (:#{#search.isAvailable} IS NULL
    OR b.isAvailable = :#{#search.isAvailable})

AND (
    :#{#search.categoryList} IS NULL
    OR c.id IN :#{#search.categoryList}
)

AND (
    :#{#search.authors} IS NULL
    OR a.id IN :#{#search.authors}
)
""")
    Page<Book> searchBook(@Param("search") Search search, Pageable pageable);
}
