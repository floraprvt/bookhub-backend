package fr.eni.bookhubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.eni.bookhubbackend.entity.bo.Author;
import fr.eni.bookhubbackend.entity.bo.Book;
import fr.eni.bookhubbackend.entity.bo.Category;
import fr.eni.bookhubbackend.repository.AuthorRepository;
import fr.eni.bookhubbackend.repository.BookRepository;
import fr.eni.bookhubbackend.repository.CategoryRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class BookControllerIT {

    private static final Faker faker = new Faker();
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired private WebApplicationContext context;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private CategoryRepository categoryRepository;

    private MockMvc mockMvc;

    private Author author;
    private Category category;
    private Book book;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        author = authorRepository.save(Author.builder()
                .firstName(faker.name().firstName() + UUID.randomUUID())
                .lastName(faker.name().lastName() + UUID.randomUUID())
                .build());

        category = categoryRepository.save(Category.builder()
                .name(faker.book().genre() + UUID.randomUUID())
                .build());

        book = bookRepository.save(Book.builder()
                .title("Integration Test Book " + UUID.randomUUID())
                .isbn(faker.numerify("#############"))
                .author(List.of(author))
                .category(List.of(category))
                .description("Description de test")
                .date(LocalDate.of(2020, 1, 1))
                .isAvailable(true)
                .build());
    }

    @Test
    @WithMockUser
    void GET_books_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    @WithMockUser
    void GET_bookById_shouldReturnBook() throws Exception {
        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));
    }

    @Test
    @WithMockUser
    void GET_bookById_unknownId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void POST_book_shouldCreateAndReturn201() throws Exception {
        Book newBook = Book.builder()
                .title("Nouveau Livre " + UUID.randomUUID())
                .isbn(faker.numerify("#############"))
                .author(List.of(author))
                .category(List.of(category))
                .description("Nouvelle description")
                .date(LocalDate.of(2024, 6, 15))
                .build();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(newBook.getTitle()))
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void DELETE_book_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(book.getId().toString())));
    }

    @Test
    @WithMockUser
    void DELETE_book_asUser_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", book.getId()))
                .andExpect(status().isForbidden());
    }
}