package fr.eni.bookhubbackend.entity.dto;

import lombok.Builder;

import java.time.Year;
import java.util.List;

@Builder
public record BookDto(String title, List<AuthorDto> author, String isbn, List<CategoryDto> category, String description, Year date, String image, Boolean isAvailable) {
}
