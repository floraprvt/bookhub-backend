package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.bo.Author;
import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Category;
import fr.eni.bookhubbackend.entity.dto.Search;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookRepositoryTest {

    private static final Faker faker = new Faker();

    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Author author;
    private Category category;
    private Book availableBook;
    private Book unavailableBook;

    @BeforeEach
    void setUp() {
        // UUID pour éviter les conflits de contrainte unique sur (firstName, lastName)
        author = authorRepository.save(Author.builder()
                .firstName(faker.name().firstName() + UUID.randomUUID())
                .lastName(faker.name().lastName() + UUID.randomUUID())
                .build());

        category = categoryRepository.save(Category.builder()
                .name(faker.book().genre() + UUID.randomUUID())
                .build());

        availableBook = bookRepository.save(Book.builder()
                .title("Les Misérables-" + UUID.randomUUID())
                .isbn(faker.numerify("#############"))
                .author(List.of(author))
                .category(List.of(category))
                .description("Un classique")
                .date(LocalDate.of(1862, 1, 1))
                .isAvailable(true)
                .build());

        unavailableBook = bookRepository.save(Book.builder()
                .title("Notre-Dame-" + UUID.randomUUID())
                .isbn(faker.numerify("#############"))
                .author(List.of(author))
                .category(List.of(category))
                .description("Un autre classique")
                .date(LocalDate.of(1831, 1, 1))
                .isAvailable(false)
                .build());
    }

    @Test
    void searchBook_noFilter_shouldReturnAllBooks() {
        Search search = new Search(null, null, null, null, null, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getId)
                .contains(availableBook.getId(), unavailableBook.getId());
    }

    @Test
    void searchBook_byTitle_shouldReturnMatchingBooks() {
        Search search = new Search("Les Misérables", null, null, null, null, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getId)
                .contains(availableBook.getId())
                .doesNotContain(unavailableBook.getId());
    }

    @Test
    void searchBook_byIsbn_shouldReturnMatchingBook() {
        Search search = new Search(null, null, null, null, null, availableBook.getIsbn());

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getId)
                .contains(availableBook.getId());
    }

    @Test
    void searchBook_byAvailability_shouldOnlyReturnAvailableBooks() {
        Search search = new Search(null, null, null, null, true, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getIsAvailable)
                .containsOnly(true);
    }

    @Test
    void searchBook_byCategory_shouldReturnBooksInCategory() {
        Search search = new Search(null, List.of(category.getId()), null, null, null, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getId)
                .contains(availableBook.getId(), unavailableBook.getId());
    }

    @Test
    void searchBook_byAuthor_shouldReturnBooksFromAuthor() {
        Search search = new Search(null, null, List.of(author.getId()), null, null, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getId)
                .contains(availableBook.getId(), unavailableBook.getId());
    }

    @Test
    void searchBook_byDate_shouldReturnBooksPublishedOnDate() {
        Search search = new Search(null, null, null, LocalDate.of(1862, 1, 1), null, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 100));

        assertThat(result.getContent()).extracting(Book::getId)
                .contains(availableBook.getId())
                .doesNotContain(unavailableBook.getId());
    }

    @Test
    void searchBook_noMatchingTitle_shouldReturnEmpty() {
        Search search = new Search("TITRE_INEXISTANT_XYZ", null, null, null, null, null);

        Page<Book> result = bookRepository.searchBook(search, PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(Book::getId)
                .doesNotContain(availableBook.getId(), unavailableBook.getId());
    }

    @Test
    void findAll_withPagination_shouldRespectPageSize() {
        Page<Book> page1 = bookRepository.findAll(PageRequest.of(0, 1));

        assertThat(page1.getContent()).hasSize(1);
        assertThat(page1.getTotalElements()).isGreaterThanOrEqualTo(2);
    }
}
