package fr.eni.bookhubbackend.entity.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Builder
public record BookDto(Long id, String title, List<AuthorDto> author, String isbn, List<CategoryDto> category, String description, LocalDate date, String image, Boolean isAvailable) {
}
