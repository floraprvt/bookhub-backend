package fr.eni.bookhubbackend.entity.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @Column(length = 13, nullable = false)
    private String isbn;

    @NotNull
    @ManyToMany
    private List<Author> author;

    private String description;

    @NotNull
    @ManyToMany
    private List<Category> category;

    private String image;

    private LocalDate date;

    private boolean isAvailable = true;
}
